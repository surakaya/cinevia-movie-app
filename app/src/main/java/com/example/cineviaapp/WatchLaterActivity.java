package com.example.cineviaapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson; // JSON işlemleri için
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WatchLaterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WatchLaterAdapter adapter;
    List<Movie> watchLaterMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later); // XML dosyasının adı doğru mu?

        recyclerView = findViewById(R.id.watchLaterRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("WatchLaterPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll(); // Tüm kayıtları al

        watchLaterMovies = new ArrayList<>();
        Gson gson = new Gson(); // Gson objesi

        // SharedPreferences'taki her kaydı dön
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Object value = entry.getValue();

            // Eğer değer String ise (yani JSON string'i)
            if (value instanceof String) {
                String movieJson = (String) value;
                try {
                    Movie movie = gson.fromJson(movieJson, Movie.class); // JSON'dan Movie objesine dönüştür
                    if (movie != null) {
                        watchLaterMovies.add(movie);
                    }
                } catch (Exception e) {
                    // JSON ayrıştırma hatası olursa
                    e.printStackTrace();
                    // Hatalı kaydı silmek isteyebilirsin: prefs.edit().remove(entry.getKey()).apply();
                }
            }
            // Diğer tiplerdeki (eski boolean gibi) kayıtları görmezden gel
        }

        adapter = new WatchLaterAdapter(this, watchLaterMovies);
        recyclerView.setAdapter(adapter);
    }

    // Activity tekrar görünür olduğunda listeyi yenilemek için
    @Override
    protected void onResume() {
        super.onResume();
        // onCreate içindeki listenin yenilenmesi için kodu buraya taşımak daha performanslı olabilir
        // veya basitçe yeniden yükle:
        loadWatchLaterMovies();
    }

    private void loadWatchLaterMovies() {
        SharedPreferences prefs = getSharedPreferences("WatchLaterPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        watchLaterMovies.clear(); // Mevcut listeyi temizle
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String movieJson = (String) value;
                try {
                    Movie movie = gson.fromJson(movieJson, Movie.class);
                    if (movie != null) {
                        watchLaterMovies.add(movie);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Adapter'a veri setinin değiştiğini bildir
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}