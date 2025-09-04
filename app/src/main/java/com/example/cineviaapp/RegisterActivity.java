package com.example.cineviaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, email, birthDate, password;
    RadioGroup genderGroup;
    Spinner interestSpinner;
    Button registerButton;
    CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        birthDate = findViewById(R.id.birthDate);
        password = findViewById(R.id.password);
        genderGroup = findViewById(R.id.genderGroup);
        interestSpinner = findViewById(R.id.interestSpinner);
        registerButton = findViewById(R.id.RegisterButton);
        rememberMeCheckBox = findViewById(R.id.chcremember);

        String[] interests = {"Sinema", "Dizi", "Belgesel", "Animasyon", "Aksiyon", "Bilim Kurgu", "Korku"};
        ArrayAdapter<String> interestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, interests);
        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interestSpinner.setAdapter(interestAdapter);

        birthDate.setOnClickListener(v -> showDatePickerDialog());

        registerButton.setOnClickListener(v -> {
            String name = fullName.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userBirthDate = birthDate.getText().toString().trim();
            String userInterest = interestSpinner.getSelectedItem().toString();
            String userPassword = password.getText().toString().trim();

            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            String gender = "";

            if (selectedGenderId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedGenderId);
                gender = selectedRadioButton.getText().toString();
            }

            if (isFormValid(name, userEmail, userPassword, userBirthDate, gender, userInterest)) {
                saveUserData(name, userEmail, userPassword, userBirthDate, gender, userInterest);

                String dialogMessage = "Ad Soyad: " + name + "\n" +
                        "E-posta: " + userEmail + "\n" +
                        "Doğum Tarihi: " + userBirthDate + "\n" +
                        "Cinsiyet: " + gender + "\n" +
                        "İlgi Alanı: " + userInterest;

                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Kayıt Başarılı")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(dialogMessage)
                        .setCancelable(false)
                        .setPositiveButton("Tamam", (dialog, which) -> finish())
                        .show();
            } else {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Hata")
                        .setMessage("Lütfen tüm alanları doldurunuz ve geçerli bir e-posta adresi ile şifre giriniz.")
                        .setPositiveButton("Tamam", null)
                        .show();
            }
        });
    }

    private boolean isFormValid(String name, String userEmail, String password, String userBirthDate, String gender, String interest) {
        if (name.isEmpty() || userEmail.isEmpty() || password.isEmpty() || userBirthDate.isEmpty() || gender.isEmpty() || interest.isEmpty()) {
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Geçerli bir e-posta girin!");
            return false;
        }
        if (password.length() < 6) {
            this.password.setError("Şifre en az 6 karakter olmalıdır!");
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> birthDate.setText(d + "/" + (m + 1) + "/" + y), year, month, day);
        datePickerDialog.show();
    }

    private void saveUserData(String name, String userEmail, String password, String userBirthDate, String gender, String interest) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (rememberMeCheckBox.isChecked()) {
            editor.putString("name", name);
            editor.putString("email", userEmail);
            editor.putString("password", password);
            editor.putString("birthDate", userBirthDate);
            editor.putString("gender", gender);
            editor.putString("interest", interest);
            editor.putBoolean("rememberMe", true);
        } else {
            editor.clear();
        }

        editor.apply();
        Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
    }
}
