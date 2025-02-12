package com.example.readingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.readingapp.model.Account;
import com.example.readingapp.admin.adminChart;
import com.example.readingapp.user.userMain;

public class LogIn extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize views
        edtUsername = findViewById(R.id.Edt_Username);
        edtPassword = findViewById(R.id.Edt_Password);
        btnLogin = findViewById(R.id.Btn_Login);
        btnRegister = findViewById(R.id.Btn_Register);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Login button click event
        btnLogin.setOnClickListener(v -> handleLogin());

        // Register button click event
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LogIn.this, Register.class)));
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Tài khoản không tồn tại");
            return;
        }

        Account account = checkLogin(username, password);
        if (account != null) {
            showToast("Đăng nhập thành công");

            // Determine next screen based on account type
            Class<?> destinationClass = (account.getType() == 3) ? adminChart.class : userMain.class;
            Intent intent = new Intent(LogIn.this, destinationClass);

            // Pass account ID as an extra
            intent.putExtra("account_id", account.getId());
            startActivity(intent);
            finish();
        } else {
            showToast("Sai tên hoặc mật khẩu");
        }
    }

    private Account checkLogin(String username, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, password, type FROM Accounts WHERE username=?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex("password");
            int typeIndex = cursor.getColumnIndex("type");
            int idIndex = cursor.getColumnIndex("id");

            if (passwordIndex >= 0 && typeIndex >= 0 && idIndex >= 0) {
                String storedPassword = cursor.getString(passwordIndex);
                int accountType = cursor.getInt(typeIndex);
                int accountId = cursor.getInt(idIndex);
                cursor.close();

                if (storedPassword.equals(password)) {
                    return new Account(accountId, accountType);
                }
            }
        }
        cursor.close();
        return null; // Incorrect credentials
    }

    private void showToast(String message) {
        Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
    }
}
