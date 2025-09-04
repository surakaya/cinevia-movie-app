package com.example.cineviaapp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatBot extends AppCompatActivity {

    private LinearLayout messageContainer;
    private EditText inputMessage;
    private ImageButton sendButton;
    private ScrollView scrollView;
    private RequestQueue requestQueue;

    // OpenRouter API key buraya gelecek
    private final String OPENROUTER_API_KEY = "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"; // ← kendi API anahtarını buraya yapıştır

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        messageContainer = findViewById(R.id.messageContainer);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);

        requestQueue = Volley.newRequestQueue(this);

        addBotMessage("👋 Merhaba! Ruh halini analiz edebilmem için bana bir şeyler yazabilirsin.");

        sendButton.setOnClickListener(v -> {
            String userInput = inputMessage.getText().toString().trim();
            if (!userInput.isEmpty()) {
                addUserMessage(userInput);
                inputMessage.setText("");
                sendMessageToOpenRouter(userInput);
            }
        });
    }

    private void addUserMessage(String message) {
        addMessageBubble(message, true);
    }

    private void addBotMessage(String message) {
        addMessageBubble(message, false);
    }

    private void addMessageBubble(String message, boolean isUser) {
        LinearLayout bubbleLayout = new LinearLayout(this);
        bubbleLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.setMargins(8, 8, 8, 8);
        bubbleParams.gravity = isUser ? Gravity.END : Gravity.START;

        TextView messageText = new TextView(this);
        messageText.setText(message);
        messageText.setTextSize(16);
        messageText.setTextColor(isUser ? 0xFFFFFFFF : 0xFFDDDDDD);
        messageText.setBackgroundResource(isUser ? R.drawable.chat_bubble_user : R.drawable.chat_bubble_bot);
        messageText.setPadding(24, 16, 24, 16);
        messageText.setLayoutParams(bubbleParams);

        TextView timeText = new TextView(this);
        timeText.setText(getCurrentTime());
        timeText.setTextSize(12);
        timeText.setTextColor(0xFF888888);
        timeText.setGravity(isUser ? Gravity.END : Gravity.START);
        timeText.setPadding(16, 4, 16, 0);

        bubbleLayout.addView(messageText);
        bubbleLayout.addView(timeText);

        messageContainer.addView(bubbleLayout);

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void sendMessageToOpenRouter(String userMessage) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-4-turbo"); // Ücretsiz ve güçlü model

            JSONArray messagesArray = new JSONArray();

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messagesArray.put(userMsg);

            jsonBody.put("messages", messagesArray);
            jsonBody.put("max_tokens", 150);
            jsonBody.put("temperature", 0.7);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                            String botReply = message.getString("content").trim();
                            addBotMessage(botReply);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            addBotMessage("⚠️ Yanıt çözümlemede bir hata oluştu.");
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                        if (statusCode == 429) {
                            addBotMessage("⚠️ API isteği limiti aşıldı. Lütfen biraz bekleyip tekrar deneyin.");
                        } else {
                            addBotMessage("⚠️ API isteği başarısız oldu. Kod: " + statusCode);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + "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"); // ← kendi API key'in
                    headers.put("Content-Type", "application/json");
                    headers.put("HTTP-Referer", "https://openrouter.ai"); // Gerekli
                    headers.put("X-Title", "CineviaApp"); // İsteğe bağlı bir başlık
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            addBotMessage("⚠️ API isteği oluşturulamadı.");
        }
    }
}
