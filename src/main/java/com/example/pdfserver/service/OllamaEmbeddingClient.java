package com.example.pdfserver.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class OllamaEmbeddingClient {
    private final OkHttpClient client = new OkHttpClient();

    public List<Float> getEmbedding(String text) throws IOException {

        JSONObject json = new JSONObject();
        json.put("model", "nomic-embed-text");
        json.put("prompt", text);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/embeddings")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JSONObject responseJson = new JSONObject(response.body().string());
            JSONArray embeddingArray = responseJson.getJSONArray("embedding");

            List<Float> embedding = new ArrayList<>();
            for (int i = 0; i < embeddingArray.length(); i++) {
                embedding.add((float) embeddingArray.getDouble(i));
            }

            return embedding;
        }
    }

    // Escape quotes in the prompt to avoid JSON errors
    private String escapeJson(String s) {
        return s.replace("\"", "\\\"");
    }
}
