package com.example.cineviaapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson; // Yeni eklendi: JSON işlemleri için
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FavoritesAdapter adapter;
    List<Movie> favoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("MoviePrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll(); // Tüm kayıtları al

        favoriteMovies = new ArrayList<>();
        Gson gson = new Gson(); // Gson objesi

        // SharedPreferences'taki her kaydı dön
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            // **Önemli**: Buradaki değer Object tipindedir. String'e cast etmeden önce tipini kontrol et.
            // Eski boolean kayıtlar (eğer temizlemeyi unuttuysak veya başka bir yerde hala boolean kaydediliyorsa)
            // ClassCastException hatasına neden olabilir.
            Object value = entry.getValue();

            if (value instanceof String) { // Eğer değer String ise (JSON string'i olmalı)
                String movieJson = (String) value;
                try {
                    Movie movie = gson.fromJson(movieJson, Movie.class); // JSON'dan Movie objesine dönüştür
                    if (movie != null) {
                        favoriteMovies.add(movie);
                    }
                } catch (Exception e) {
                    // JSON ayrıştırma hatası olursa buraya düşeriz.
                    // Örneğin, bozuk bir JSON kaydedildiyse.
                    e.printStackTrace();
                    // Opsiyonel: Hatalı kaydı SharedPreferences'tan silmek için editor.remove(entry.getKey()).apply();
                }
            }
            // Eski boolean kayıtları burada atlanacak, ClassCastException oluşmayacak.
            // else if (value instanceof Boolean) {
            //     // Eski boolean kayıtları bu blokta işlenebilir, ancak amacımız JSON kaydetmek.
            //     // Şimdilik sadece ignore ediyoruz.
            // }
        }

        adapter = new FavoritesAdapter(this, favoriteMovies);
        recyclerView.setAdapter(adapter);
    }
}