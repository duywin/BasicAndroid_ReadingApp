package com.example.readingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.readingapp.model.Account;

public class Register extends AppCompatActivity {
    private EditText edtEmail, edtName, edtPass, edtRePass, edtDOB;
    private RadioGroup groupGender;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        edtEmail = findViewById(R.id.Edt_Email);
        edtName = findViewById(R.id.Edt_Username);
        edtPass = findViewById(R.id.Edt_Password);
        edtRePass = findViewById(R.id.Edt_RePassword);
        edtDOB = findViewById(R.id.Edt_DOB);
        groupGender = findViewById(R.id.GroupGender);
        btnRegister = findViewById(R.id.Btn_Register);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String username = edtName.getText().toString().trim();
        String password = edtPass.getText().toString();
        String confirmPassword = edtRePass.getText().toString();
        String dob = edtDOB.getText().toString().trim();
        int selectedGenderId = groupGender.getCheckedRadioButtonId();
        boolean gender = selectedGenderId == R.id.Btn_Nam; // true for Nam, false for Nữ
        int type = 1; // Always 1

        // Validation Checks
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty() || selectedGenderId == -1) {
            showToast("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ.");
            return;
        }

        if (password.length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Mật khẩu xác nhận không khớp.");
            return;
        }

        if (dbHelper.isUsernameExists(username)) {
            showToast("Tên đăng nhập đã tồn tại.");
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            showToast("Email đã tồn tại.");
            return;
        }

        // Insert account into database
        Account newAccount = new Account(0, username, email, password, dob, gender, type);
        if (dbHelper.insertAccount(newAccount)) {
            showToast("Đăng ký thành công!");
            Intent intent = new Intent( Register.this, LogIn.class);
            startActivity(intent);
        }
        else {
            showToast("Đăng ký thất bại, thử lại.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
