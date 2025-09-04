package com.example.cineviaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetail extends AppCompatActivity {

    private static final String API_KEY = "6899ac0ff0fb9dbbf05efbec9c8187b2";

    ImageView imagePoster;
    TextView  textTitle, textDetails, textMeta, textCast;
    CheckBox  checkBoxFavorite, chcDahasonra;

    private Movie currentMovie; // Detaylarını gösterdiğimiz film objesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        imagePoster      = findViewById(R.id.imagePoster);
        textTitle        = findViewById(R.id.textTitle);
        textDetails      = findViewById(R.id.textDetails);
        textMeta         = findViewById(R.id.textMeta);
        textCast         = findViewById(R.id.textCast);
        checkBoxFavorite = findViewById(R.id.chcFavoriler);
        chcDahasonra     = findViewById(R.id.chcDahasonra);

        // ───── Intent verileri (Diğer Activity'den gelen film bilgileri) ─────
        int    movieId    = getIntent().getIntExtra("movie_id", -1);
        String title      = getIntent().getStringExtra("title");
        String overview   = getIntent().getStringExtra("overview");
        String posterPath = getIntent().getStringExtra("poster_path");
        String imageUrl   = "https://image.tmdb.org/t/p/w500" + posterPath;

        // currentMovie objesini burada oluşturuyoruz.
        currentMovie = new Movie(movieId, title, "", imageUrl, overview, posterPath);

        // ───── UI’ye verileri yerleştir ─────
        Picasso.get().load(imageUrl).into(imagePoster);
        textTitle.setText(title);
        textDetails.setText(overview);

        // ───── Favoriler Ayarı (Önceki haliyle aynı) ─────
        SharedPreferences favPrefs = getSharedPreferences("MoviePrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        boolean isFavorite = favPrefs.contains(String.valueOf(currentMovie.getMovieId()));
        checkBoxFavorite.setChecked(isFavorite);

        checkBoxFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = favPrefs.edit();

                if (isChecked) {
                    String movieJson = gson.toJson(currentMovie);
                    editor.putString(String.valueOf(currentMovie.getMovieId()), movieJson);
                    Toast.makeText(MovieDetail.this, currentMovie.getTitle() + " favorilere eklendi!", Toast.LENGTH_SHORT).show();
                } else {
                    editor.remove(String.valueOf(currentMovie.getMovieId()));
                    Toast.makeText(MovieDetail.this, currentMovie.getTitle() + " favorilerden çıkarıldı.", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
            }
        });

        // ───── Daha Sonra İzle Ayarı (YENİLENEN KISIM) ─────
        SharedPreferences laterPrefs = getSharedPreferences("WatchLaterPrefs", MODE_PRIVATE);
        // Filmin zaten "Daha Sonra İzle" listesinde olup olmadığını kontrol et
        boolean isWatchLater = laterPrefs.contains(String.valueOf(currentMovie.getMovieId()));
        chcDahasonra.setChecked(isWatchLater);

        chcDahasonra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = laterPrefs.edit();

                if (isChecked) {
                    // CheckBox işaretlendi: Filmi "Daha Sonra İzle" listesine ekle
                    String movieJson = gson.toJson(currentMovie);
                    // Filmi movieId'si ile SharedPreferences'a kaydet
                    editor.putString(String.valueOf(currentMovie.getMovieId()), movieJson);
                    Toast.makeText(MovieDetail.this, currentMovie.getTitle() + " daha sonra izlenecek!", Toast.LENGTH_SHORT).show();
                } else {
                    // CheckBox işareti kaldırıldı: Filmi listeden çıkar
                    editor.remove(String.valueOf(currentMovie.getMovieId()));
                    Toast.makeText(MovieDetail.this, currentMovie.getTitle() + " izleme listesinden çıkarıldı.", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
            }
        });

        // ───── Film meta + oyuncu kadrosu TMDB’den çek ─────
        if (movieId != -1) fetchMovieExtra(movieId);
    }

    /** TMDB “/movie/{id}” ve “/movie/{id}/credits” çağrıları */
    private void fetchMovieExtra(int movieId) {
        RequestQueue q = Volley.newRequestQueue(this);

        // ▸ Detay (süre, yıl, türler)
        String urlDetails = "https://api.themoviedb.org/3/movie/" + movieId +
                "?api_key=" + API_KEY + "&language=tr-TR";
        q.add(new JsonObjectRequest(Request.Method.GET, urlDetails, null,
                obj -> {
                    try {
                        int    runtime   = obj.optInt("runtime");
                        String relDate   = obj.optString("release_date");
                        String year      = relDate.length() >= 4 ? relDate.substring(0,4) : "—";

                        JSONArray genresArr = obj.getJSONArray("genres");
                        StringBuilder gBuf = new StringBuilder();
                        String genreNames = "";
                        for (int i=0;i<genresArr.length();i++){
                            if (i>0) gBuf.append(", ");
                            String currentGenreName = genresArr.getJSONObject(i).getString("name");
                            gBuf.append(currentGenreName);
                            // Sadece ilk tür adını alıyoruz veya istediğiniz gibi birleştiriyoruz
                            if (i == 0) {
                                genreNames = currentGenreName;
                            } else {
                                genreNames += ", " + currentGenreName; // Tüm türleri virgülle ayır
                            }
                        }
                        String meta = year + "  •  " + runtime + " dk  •  " + gBuf;
                        textMeta.setText(meta);

                        // currentMovie objesinin 'genre' bilgisini güncelliyoruz.
                        if (currentMovie != null) {
                            currentMovie = new Movie(
                                    currentMovie.getMovieId(),
                                    currentMovie.getTitle(),
                                    genreNames, // API'den gelen güncel tür bilgisi
                                    currentMovie.getPosterUrl(), // URL hala eski haliyle durabilir
                                    currentMovie.getOverview(),
                                    currentMovie.getPosterPath()
                            );
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                err -> {}
        ));

        // ▸ Oyuncu kadrosu
        String urlCredits = "https://api.themoviedb.org/3/movie/" + movieId +
                "/credits?api_key=" + API_KEY + "&language=tr-TR";
        q.add(new JsonObjectRequest(Request.Method.GET, urlCredits, null,
                obj -> {
                    try {
                        JSONArray castArr = obj.getJSONArray("cast");
                        StringBuilder cBuf = new StringBuilder();
                        for (int i=0;i<Math.min(5, castArr.length()); i++){
                            if (i>0) cBuf.append(", ");
                            cBuf.append(castArr.getJSONObject(i).getString("name"));
                        }
                        textCast.setText("Oyuncular: " + cBuf);
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                err -> {}
        ));
    }
}