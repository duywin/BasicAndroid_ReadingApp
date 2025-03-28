package com.example.readingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.readingapp.dao.AccountDAO;
import com.example.readingapp.model.Account;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Register extends AppCompatActivity {
    private EditText edtEmail, edtName, edtPass, edtRePass, edtDOB;
    private RadioGroup groupGender;
    private Button btnRegister;
    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountDAO = new AccountDAO(this);

        edtEmail = findViewById(R.id.Edt_Email);
        edtName = findViewById(R.id.Edt_Username);
        edtPass = findViewById(R.id.Edt_Password);
        edtRePass = findViewById(R.id.Edt_RePassword);
        edtDOB = findViewById(R.id.Edt_DOB);
        groupGender = findViewById(R.id.GroupGender);
        btnRegister = findViewById(R.id.Btn_Register);

        btnRegister.setOnClickListener(v -> registerUser());
        edtDOB.setOnClickListener(v -> showDatePicker());

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
            edtDOB.setText(selectedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String username = edtName.getText().toString().trim();
        String password = edtPass.getText().toString();
        String confirmPassword = edtRePass.getText().toString();
        String dob = edtDOB.getText().toString().trim();
        int selectedGenderId = groupGender.getCheckedRadioButtonId();
        boolean gender = selectedGenderId == R.id.Btn_Nam; // true for Nam, false for Nữ
        int type = 1; // Always 1 (Normal User)

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

        if (accountDAO.isUsernameExists(username)) {
            showToast("Tên đăng nhập đã tồn tại.");
            return;
        }

        if (accountDAO.isEmailExists(email)) {
            showToast("Email đã tồn tại.");
            return;
        }

        // Insert account into database
        Account newAccount = new Account(username, email, password, dob, gender, type);
        if (accountDAO.insertAccount(newAccount)) {
            showToast("Đăng ký thành công!");
            Intent intent = new Intent(Register.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            showToast("Đăng ký thất bại, thử lại.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
