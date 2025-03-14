package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.AccountDAO;
import com.example.readingapp.model.Account;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class inputAccount extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etDob;
    private RadioGroup rgGender, rgType;
    private Button btnSave, btnCancel;
    private AccountDAO accountDAO;
    private int accountId = -1; // Default: New account

    private int logaccountId;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_account);

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDob = findViewById(R.id.etDob);
        rgGender = findViewById(R.id.rgGender);
        rgType = findViewById(R.id.rgType);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        accountDAO = new AccountDAO(this);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        logaccountId = sharedPreferences.getInt("account_id", -1);

        if (logaccountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        // Handle date picker for DOB
        etDob.setOnClickListener(v -> showDatePicker());

        // Check if editing an existing account
        if (getIntent().hasExtra("ACCOUNT_ID")) {
            accountId = getIntent().getIntExtra("ACCOUNT_ID", -1);
            loadAccountData(accountId);
        }

        btnSave.setOnClickListener(v -> saveAccount());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        // Set date constraints (Only past dates allowed)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now());

        // Create the date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default selection
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // Show picker and get selected date
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = sdf.format(new Date(selection));
            etDob.setText(selectedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void loadAccountData(int id) {
        Account account = accountDAO.getAccountById(id);
        if (account != null) {
            etUsername.setText(account.getUsername());
            etEmail.setText(account.getEmail());
            etPassword.setText(account.getPassword());
            etDob.setText(account.getDob());

            // Set gender
            if (account.isGender()) {
                rgGender.check(R.id.rbMale);
            } else {
                rgGender.check(R.id.rbFemale);
            }

            // Set account type
            switch (account.getType()) {
                case 3:
                    rgType.check(R.id.rbAdmin);
                    break;
                case 2:
                    rgType.check(R.id.rbVip);
                    break;
                case 1:
                default:
                    rgType.check(R.id.rbNormal);
                    break;
            }
        }
    }

    private void saveAccount() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String dob = etDob.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale;

        int type;
        int selectedType = rgType.getCheckedRadioButtonId();
        if (selectedType == R.id.rbAdmin) {
            type = 3;
        } else if (selectedType == R.id.rbVip) {
            type = 2;
        } else {
            type = 1;
        }

        if (accountId == -1) {
            // New account
            boolean success = accountDAO.insertAccount(username, email, password, dob, gender, type);
            if (success) {
                Toast.makeText(this, "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm tài khoản!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing account
            boolean success = accountDAO.updateAccount(accountId, username, email, password, dob, gender, type);
            if (success) {
                Toast.makeText(this, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật tài khoản!", Toast.LENGTH_SHORT).show();
            }
        }

        setResult(RESULT_OK);
        finish();
    }
}
