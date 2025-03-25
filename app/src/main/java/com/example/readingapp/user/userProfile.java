package com.example.readingapp.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.AccountDAO;
import com.example.readingapp.dao.SubscriptionDAO;
import com.example.readingapp.model.Account;
import com.example.readingapp.model.Subscription;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class userProfile extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private AccountDAO accountDAO;
    private SubscriptionDAO subscriptionDAO;
    private Account account;
    private int accountId;

    private TextView tvUsername, tvEmail, tvDob, tvGender, tvVipStatus;
    private EditText etUsername, etEmail, etDob;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnEdit, btnSave, btnCancel, btnRegisterVip;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeComponents();
        retrieveAccountData();
        setupButtonListeners();
        setupBottomNavigation();
    }

    /** Initialize UI components */
    private void initializeComponents() {
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountDAO = new AccountDAO(this);
        subscriptionDAO = new SubscriptionDAO(this);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);
        tvVipStatus = findViewById(R.id.tvVipStatus);
        btnRegisterVip = findViewById(R.id.btnRegisterVip);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etDob = findViewById(R.id.etDob);

        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    /** Retrieve and display user data */
    private void retrieveAccountData() {
        accountId = sharedPreferences.getInt("account_id", -1);
        if (accountId == -1) {
            showErrorAndRedirect("Lỗi: Không tìm thấy tài khoản!");
            return;
        }

        account = accountDAO.getAccountById(accountId);
        if (account == null) {
            showErrorAndRedirect("Lỗi: Không tìm thấy tài khoản!");
            return;
        }

        displayUserData();
    }

    /** Display user information */
    private void displayUserData() {
        tvUsername.setText(account.getUsername());
        tvEmail.setText(account.getEmail());
        tvDob.setText(account.getDob());
        tvGender.setText(account.isGender() ? "Nam" : "Nữ");

        boolean isVip = account.getType() == 2;
        tvVipStatus.setVisibility(isVip ? View.VISIBLE : View.GONE);
        btnRegisterVip.setVisibility(isVip ? View.GONE : View.VISIBLE);
        tvVipStatus.setText(isVip ? "VIP" : "");

        toggleEditMode(false);
    }

    /** Setup button click listeners */
    private void setupButtonListeners() {
        btnEdit.setOnClickListener(v -> toggleEditMode(true));
        btnSave.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> toggleEditMode(false));
        btnRegisterVip.setOnClickListener(v -> showSubscriptionDialog());
    }

    /** Show subscription options */
    private void showSubscriptionDialog() {
        final String[] options = {"1 tháng", "3 tháng", "6 tháng"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn gói đăng ký")
                .setItems(options, (dialog, which) -> handleSubscription(which == 0 ? 1 : (which == 1 ? 3 : 6)))
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /** Handle subscription registration/extension */
    private void handleSubscription(int months) {
        Subscription existingSub = subscriptionDAO.getSubscriptionByAccountId(accountId);

        if (existingSub != null) {
            subscriptionDAO.updateSubscription(accountId, months);
            showToast("Đã gia hạn gói " + months + " tháng!");
        } else {
            subscriptionDAO.addSubscription(accountId, months);
            accountDAO.updateAccountType(accountId, 2);
            showToast("Đăng ký VIP thành công!");
            retrieveAccountData();
        }
    }

    /** Enable or disable edit mode */
    private void toggleEditMode(boolean enable) {
        isEditing = enable;

        tvUsername.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvEmail.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvDob.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvGender.setVisibility(enable ? View.GONE : View.VISIBLE);

        etUsername.setVisibility(enable ? View.VISIBLE : View.GONE);
        etEmail.setVisibility(enable ? View.VISIBLE : View.GONE);
        etDob.setVisibility(enable ? View.VISIBLE : View.GONE);
        rgGender.setVisibility(enable ? View.VISIBLE : View.GONE);

        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnCancel.setVisibility(enable ? View.VISIBLE : View.GONE);

        if (enable) {
            etUsername.setText(tvUsername.getText().toString());
            etEmail.setText(tvEmail.getText().toString());
            etDob.setText(tvDob.getText().toString());
            rbMale.setChecked("Nam".equals(tvGender.getText().toString()));
            rbFemale.setChecked("Nữ".equals(tvGender.getText().toString()));
        }
    }

    /** Save edited user information */
    private void saveChanges() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newDob = etDob.getText().toString().trim();
        boolean newGender = rbMale.isChecked();

        if (newUsername.isEmpty() || newEmail.isEmpty() || newDob.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (accountDAO.updateAccount(accountId, newUsername, newEmail, account.getPassword(), newDob, newGender, account.getType())) {
            showToast("Cập nhật thành công!");
            retrieveAccountData();
        } else {
            showToast("Cập nhật thất bại!");
        }
    }

    /** Setup bottom navigation */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_user_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_user_main:
                    startActivity(new Intent(this, userMain.class));
                    return true;
                case R.id.nav_user_search:
                    startActivity(new Intent(this, userSearch.class));
                    return true;
                case R.id.nav_user_favorite:
                    startActivity(new Intent(this, userFavorite.class));
                    return true;
                case R.id.nav_user_profile:
                    return true;
            }
            return false;
        });
    }

    /** Show error and redirect to login */
    private void showErrorAndRedirect(String message) {
        showToast(message);
        startActivity(new Intent(this, LogIn.class));
        finish();
    }

    /** Show a toast message */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
