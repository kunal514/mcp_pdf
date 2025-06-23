package com.example.pdfserver.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OllamaChatClient {

    private final OkHttpClient client = new OkHttpClient();

    public String askOllama(String prompt) throws IOException {
        String model = "mistral"; // or llama3, gemma, etc.

        String json = "{\n" +
                "  \"model\": \"" + model + "\",\n" +
                "  \"prompt\": \"" + prompt.replace("\"", "\\\"") + "\",\n" +
                "  \"stream\": false\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Ollama chat error: " + response);

            String responseBody = response.body().string();

            // Get just the "response" field if it's wrapped in JSON
            int responseStart = responseBody.indexOf("\"response\":\"");
            if (responseStart != -1) {
                int start = responseStart + "\"response\":\"".length();
                int end = responseBody.indexOf("\"", start);
                return responseBody.substring(start, end).replace("\\n", "\n");
            }

            return responseBody; // fallback
        }
    }
}
