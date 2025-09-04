package com.example.cineviaapp;

import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Layout bileşenlerini buluyoruz
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        registerBtn = findViewById(R.id.RegisterButton);

        // SharedPreferences'ten verileri al
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        loginBtn.setOnClickListener(v -> {
            String enteredEmailOrName = email.getText().toString().trim();
            String enteredPassword = password.getText().toString().trim();

            String savedEmail = sharedPreferences.getString("email", "");
            String savedName = sharedPreferences.getString("name", "");
            String savedPassword = sharedPreferences.getString("password", "");

            // Giriş kontrolü: isim veya e-posta doğruysa ve şifre eşleşiyorsa
            boolean isEmailMatch = enteredEmailOrName.equals(savedEmail);
            boolean isNameMatch = enteredEmailOrName.equals(savedName);
            boolean isPasswordMatch = enteredPassword.equals(savedPassword);

            if ((isEmailMatch || isNameMatch) && isPasswordMatch) {
                Toast.makeText(AuthActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AuthActivity.this, HomeScreen.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AuthActivity.this, "Kullanıcı adı veya şifre yanlış!", Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}