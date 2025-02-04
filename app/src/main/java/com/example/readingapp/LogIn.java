package com.example.readingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LogIn extends AppCompatActivity {

    EditText EdtName, EdtPass;
    Button BtnLogin, BtnRegister;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize views
        EdtName = findViewById(R.id.Edt_Username);
        EdtPass = findViewById(R.id.Edt_Password);
        BtnLogin = findViewById(R.id.Btn_Login);
        BtnRegister = findViewById(R.id.Btn_Register);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Login button click event
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = EdtName.getText().toString().trim();
                String password = EdtPass.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogIn.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    int loginStatus = checkLogin(username, password);
                    if (loginStatus == 1) {
                        Toast.makeText(LogIn.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    } else if (loginStatus == -1) {
                        Toast.makeText(LogIn.this, "Sai tên hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LogIn.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, Register.class);
                startActivity(intent);
            }
        });
    }

    // Function to check login credentials in the database
    private int checkLogin(String username, String password) {
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT password FROM Accounts WHERE username=?", new String[]{username});

        if (cursor.getCount() == 0) {
            cursor.close();
            return 0; // Account does not exist
        }

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex("password");
            if (passwordIndex >= 0) { // Ensure column exists
                String storedPassword = cursor.getString(passwordIndex);
                cursor.close();
                if (storedPassword.equals(password)) {
                    return 1; // Login success
                }
            }
        }

        cursor.close();
        return -1; // Incorrect password
    }
}