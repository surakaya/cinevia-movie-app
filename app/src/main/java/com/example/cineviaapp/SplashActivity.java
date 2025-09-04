package com.example.cineviaapp;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    ProgressBar progressBar;
    Button continueButton;
    Vibrator vibrator; // Titresim için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        continueButton = findViewById(R.id.continue_button);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Logo animasyonu
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(3000); // 3 saniyede logo büyüsün
        animatorSet.start();

        // ProgressBar ayarları
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Vibrasyon başlat
        startVibration();

        // ProgressBar'ı doldur
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int progress = i;
                runOnUiThread(() -> progressBar.setProgress(progress));
            }

            // Vibrasyonu durdur ve continue butonunu göster
            runOnUiThread(() -> {
                stopVibration();
                continueButton.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void startVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createWaveform(new long[]{0, 100, 100}, 0);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(new long[]{0, 100, 100}, 0); // Eski cihazlar için
            }
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel(); // Titreşimi durdur
        }
    }

    // Continue butonuna tıklanınca giriş ekranına geç
    public void onContinueClick(View view) {
        Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
