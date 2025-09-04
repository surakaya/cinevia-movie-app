package com.example.cineviaapp;  // kendi package adını kullan

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DuyguAnaliz extends AppCompatActivity {

    private EditText editTextUserInput;
    private Button buttonAnalyze;
    private TextView textViewResult;

    private static final String API_URL = "http://192.168.0.16:5000/analyze";
    // Bilgisayar IP adresin

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duygu_analiz);

        editTextUserInput = findViewById(R.id.editTextUserInput);
        buttonAnalyze = findViewById(R.id.buttonAnalyze);
        textViewResult = findViewById(R.id.textViewResult);

        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userText = editTextUserInput.getText().toString().trim();

                if (userText.isEmpty()) {
                    Toast.makeText(DuyguAnaliz.this, "Lütfen bir metin giriniz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                analyzeSentiment(userText);
            }
        });
    }

    private void analyzeSentiment(String text) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Hata oluştu!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, jsonBody,
                response -> {
                    try {
                        String sentiment = response.getString("sentiment");
                        JSONArray movies = response.getJSONArray("movies");

                        StringBuilder resultBuilder = new StringBuilder();
                        resultBuilder.append("Duygu: ").append(sentiment).append("\n\n");
                        resultBuilder.append("Film Önerileri:\n");

                        for (int i = 0; i < movies.length(); i++) {
                            JSONObject movie = movies.getJSONObject(i);
                            String title = movie.getString("title");
                            String releaseDate = movie.getString("release_date");
                            String overview = movie.getString("overview");

                            resultBuilder.append(i + 1).append(". ")
                                    .append(title).append(" (").append(releaseDate).append(")\n")
                                    .append(overview).append("\n\n");
                        }

                        textViewResult.setText(resultBuilder.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        textViewResult.setText("Veri işlenirken hata oluştu.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    textViewResult.setText("Sunucuya bağlanırken hata oluştu.");
                });

        queue.add(request);
    }
}
