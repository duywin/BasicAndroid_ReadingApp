package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.AccountDAO;
import com.example.readingapp.model.Account;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class adminAccount extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Account> accountList;
    private Button addAccountButton;
    private AccountAdapter accountAdapter;
    private int logaccountId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        recyclerView = findViewById(R.id.account_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addAccountButton = findViewById(R.id.add_account_button);
        addAccountButton.setOnClickListener(v -> addNewAccount());

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        logaccountId = sharedPreferences.getInt("account_id", -1);

        if (logaccountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        loadAccounts();

        setupBottomNavigation();
    }

    private void loadAccounts() {
        AccountDAO accountDAO = new AccountDAO(this);
        accountList = accountDAO.getAllAccounts();
        recyclerView.setAdapter(new AccountAdapter(accountList));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_chart) {
                startActivity(new Intent(adminAccount.this, adminChart.class));
                return true;
            } else if (id == R.id.nav_admin_story) {
                startActivity(new Intent(adminAccount.this, adminStory.class));
                return true;
            } else if (id == R.id.nav_admin_genre) {
                startActivity(new Intent(adminAccount.this, adminGenre.class));
                return true;
            } else if (id == R.id.nav_admin_account) {
                return true;
            }
            return false;
        });
    }

    private void addNewAccount() {
        Intent intent = new Intent(adminAccount.this, inputAccount.class);
        startActivity(intent);
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
        private final List<Account> accounts;

        AccountAdapter(List<Account> accounts) {
            this.accounts = accounts;
        }

        @NonNull
        @Override
        public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_account_item, parent, false);
            return new AccountViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
            Account account = accounts.get(position);
            holder.accountUsername.setText("Tên: " + account.getUsername());
            holder.accountEmail.setText("Email: " + account.getEmail());
            holder.accountDob.setText("Ngày sinh: " + account.getDob());
            holder.accountGender.setText(account.isGender() ? "Giới tính: Nam" : "Giới tính: Nữ");
            holder.accountType.setText("Loại: " + (account.getType() == 1 ? "Thường" : account.getType() == 2 ? "VIP" : "Admin"));

            holder.editButton.setOnClickListener(v -> editAccount(account.getId()));
            holder.deleteButton.setOnClickListener(v -> deleteAccount(account.getId()));
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }

        public class AccountViewHolder extends RecyclerView.ViewHolder {
            TextView accountUsername, accountEmail, accountDob, accountGender, accountType;
            Button editButton, deleteButton;

            AccountViewHolder(View itemView) {
                super(itemView);
                accountUsername = itemView.findViewById(R.id.account_username);
                accountEmail = itemView.findViewById(R.id.account_email);
                accountDob = itemView.findViewById(R.id.account_dob);
                accountGender = itemView.findViewById(R.id.account_gender);
                accountType = itemView.findViewById(R.id.account_type);
                editButton = itemView.findViewById(R.id.edit_account_button);
                deleteButton = itemView.findViewById(R.id.delete_account_button);
            }
        }
    }

    private void editAccount(int accountId) {
        Intent intent = new Intent(adminAccount.this, inputAccount.class);
        intent.putExtra("ACCOUNT_ID", accountId);
        startActivity(intent);
    }

    private void deleteAccount(int accountId) {
        AccountDAO accountDAO = new AccountDAO(this);
        boolean deleted = accountDAO.deleteAccount(accountId);
        if (deleted) {
            loadAccounts();
            Toast.makeText(this, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi xóa tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }
}
