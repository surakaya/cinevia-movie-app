package com.example.cineviaapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PERMISSIONS = 100;

    ImageView profileImage;
    Uri photoUri;
    TextView textName, textEmail, textLocation, textBirthdate, textInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textLocation = findViewById(R.id.textLocation);
        textBirthdate = findViewById(R.id.textBirthdate);
        textInterests = findViewById(R.id.textInterests);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", null);
        String email = sharedPreferences.getString("email", null);
        String birthdate = sharedPreferences.getString("birthDate", null);
        String interests = sharedPreferences.getString("interest", null);
        String location = "Türkiye";

        if (name != null) textName.setText("👤  Ad Soyad: " + name);
        if (email != null) textEmail.setText("📧  E-posta: " + email);
        if (location != null) textLocation.setText("📍  Konum: " + location);
        if (birthdate != null) textBirthdate.setText("🎂  Doğum Tarihi: " + birthdate);
        if (interests != null) textInterests.setText("🎬  İlgi Alanları: " + interests);

        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> finish());

        profileImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImagePicker();
            } else {
                requestPermissions();
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS);
    }

    private void showImagePicker() {
        String[] options = {"Galeriden Seç", "Kamera ile Çek"};
        new AlertDialog.Builder(this)
                .setTitle("Profil Fotoğrafı")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, REQUEST_GALLERY);
                    } else {
                        try {
                            File photoFile = createImageFile();
                            if (photoFile != null) {
                                photoUri = FileProvider.getUriForFile(this,
                                        getPackageName() + ".fileprovider", photoFile);
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(takePicture, REQUEST_CAMERA);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                profileImage.setImageURI(selectedImage);
            } else if (requestCode == REQUEST_CAMERA && photoUri != null) {
                profileImage.setImageURI(photoUri);
            }
        }
    }
}