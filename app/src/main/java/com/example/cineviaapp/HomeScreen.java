package com.example.cineviaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {

    private final String API_KEY = "6899ac0ff0fb9dbbf05efbec9c8187b2";
    private final String[] categories = {
            "Türk Yapımı", "En Popüler", "Aksiyon", "Komedi", "Bilim Kurgu"
    };
    private final String[] categoryQueries = {
            "&with_origin_country=TR",
            "&sort_by=popularity.desc",
            "&with_genres=28",
            "&with_genres=35",
            "&with_genres=878"
    };
    private static final int MAX_PAGES = 5; // <-- 5 sayfa yeterli (20 × 5 = 100 film)

    ImageButton btnProfile, btnLists, btnSinemalar, btnChatbot;
    Button btnfilmoner;
    LinearLayout sectionContainer;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        queue           = Volley.newRequestQueue(this);
        btnProfile      = findViewById(R.id.profileButton);
        btnLists        = findViewById(R.id.btnLists);
        btnSinemalar    = findViewById(R.id.btnSinemalar);
        btnChatbot      = findViewById(R.id.btnChatbot);
        btnfilmoner     = findViewById(R.id.btnfilmoner);
        sectionContainer= findViewById(R.id.sectionContainer);

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        btnLists.setOnClickListener(v -> showPopupMenu());
        btnSinemalar.setOnClickListener(v ->
                startActivity(new Intent(this, NearbyCinemas.class)));
        btnChatbot.setOnClickListener(v ->
                startActivity(new Intent(this, ChatBot.class)));
        btnfilmoner.setOnClickListener(v ->
                startActivity(new Intent(this, DuyguAnaliz.class)));

        /** Her kategori için ardışık (recursive) sayfa çek **/
        for (int i = 0; i < categories.length; i++) {
            fetchPagesRecursive(categories[i], categoryQueries[i], 1, new JSONArray());
        }
    }

    /** Sayfaları sırayla çeker; hepsi bitince bölümü ekler */
    private void fetchPagesRecursive(String catTitle, String query, int page, JSONArray acc) {

        if (page > MAX_PAGES) {                 // limit doldu → UI’ya bas
            addMovieSection(catTitle, acc);
            return;
        }

        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY +
                "&language=tr-TR&page=" + page + query;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                res -> {
                    try {
                        JSONArray results = res.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++)
                            acc.put(results.getJSONObject(i));

                        // Son sayfa mı? Boş döndüyse veya limit dolduysa ekrana bas
                        if (results.length() == 0 || page == MAX_PAGES) {
                            addMovieSection(catTitle, acc);
                        } else {                    // sıradaki sayfayı çek
                            fetchPagesRecursive(catTitle, query, page + 1, acc);
                        }
                    } catch (JSONException e) {
                        Log.e("ParseError", e.getMessage());
                    }
                },
                err -> Log.e("APIError", err.toString())
        );
        queue.add(req);
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(this, btnLists);
        MenuInflater inflater = popup.getMenuInflater();
        popup.getMenu().add("Daha Sonra İzle");
        popup.getMenu().add("Favoriler");

        SharedPreferences sp = getSharedPreferences("MoviePrefs", MODE_PRIVATE);
        String fav = sp.getString("favorite_movie", null);
        if (fav != null) popup.getMenu().add("Favori Film: " + fav);

        popup.setOnMenuItemClickListener(item -> {
            String t = item.getTitle().toString();
            if (t.equals("Favoriler"))
                startActivity(new Intent(this, FavoritesActivity.class));
            else if (t.startsWith("Favori Film")) {
                Intent i = new Intent(this, MovieDetail.class);
                i.putExtra("title", fav);
                startActivity(i);
            } else if (t.equals("Daha Sonra İzle"))
                startActivity(new Intent(this, WatchLaterActivity.class));
            return true;
        });
        popup.show();
    }

    /** UI’ya kategori + film afişleri basar */
    private void addMovieSection(String title, JSONArray movies) {
        runOnUiThread(() -> {
            LinearLayout section = new LinearLayout(this);
            section.setOrientation(LinearLayout.VERTICAL);
            section.setPadding(0, dpToPx(16), 0, dpToPx(16));

            TextView tv = new TextView(this);
            tv.setText(title); tv.setTextSize(20);
            tv.setTextColor(getResources().getColor(android.R.color.white));
            tv.setPadding(dpToPx(8), 0, 0, dpToPx(8));
            section.addView(tv);

            HorizontalScrollView hsv = new HorizontalScrollView(this);
            hsv.setHorizontalScrollBarEnabled(false);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dpToPx(8), 0, dpToPx(8), 0);

            for (int i = 0; i < movies.length(); i++) {
                try {
                    JSONObject m  = movies.getJSONObject(i);
                    int    id     = m.getInt("id");
                    String poster = m.optString("poster_path", "");
                    String full   = "https://image.tmdb.org/t/p/w500" + poster;
                    String t      = m.getString("title");
                    String ov     = m.getString("overview");

                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams lp =
                            new LinearLayout.LayoutParams(dpToPx(120), dpToPx(180));
                    lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
                    img.setLayoutParams(lp);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(this).load(full).into(img);

                    img.setOnClickListener(v -> {
                        Intent det = new Intent(this, MovieDetail.class);
                        det.putExtra("movie_id", id);
                        det.putExtra("title", t);
                        det.putExtra("overview", ov);
                        det.putExtra("poster_path", poster);
                        startActivity(det);
                    });
                    row.addView(img);
                } catch (JSONException e) {
                    Log.e("MovieError", e.getMessage());
                }
            }
            hsv.addView(row);
            section.addView(hsv);
            sectionContainer.addView(section);
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}