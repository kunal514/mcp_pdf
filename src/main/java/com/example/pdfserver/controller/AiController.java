package com.example.pdfserver.controller;


import com.example.pdfserver.model.VectorChunk;
import com.example.pdfserver.service.OllamaEmbeddingClient;
import com.example.pdfserver.service.VectorStoreService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ask")
public class AiController {

@Autowired
    private final OllamaEmbeddingClient embeddingClient;
    private final VectorStoreService vectorStoreService;

    @Autowired
    public AiController(OllamaEmbeddingClient embeddingClient, VectorStoreService vectorStoreService) {
        this.embeddingClient = embeddingClient;
        this.vectorStoreService = vectorStoreService;
    }

    public AiController(OllamaEmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
        vectorStoreService = null;
    }

    @GetMapping("/question")
    public String ask(@RequestParam("question") String question) {
        try {
            // Step 1: Get embedding for the question
            List<Float> questionEmbedding = embeddingClient.getEmbedding(question);

            // Step 2: Get all stored chunks
            List<VectorChunk> allChunks = vectorStoreService.getAllChunks();

            // Step 3: Find the most relevant chunk using cosine similarity
            VectorChunk bestMatch = allChunks.stream()
                    .max(Comparator.comparingDouble(c -> cosineSimilarity(questionEmbedding, c.getEmbedding())))
                    .orElse(null);

            return bestMatch != null
                    ? "Most relevant chunk:\n" + bestMatch.getText()
                    : "No relevant chunk found.";

        } catch (Exception e) {
            return "Error processing question: " + e.getMessage();
        }
    }

    private double cosineSimilarity(List<Float> a, List<Float> b) {
        if (a.size() != b.size()) return -1.0;

        double dot = 0.0, magA = 0.0, magB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            magA += a.get(i) * a.get(i);
            magB += b.get(i) * b.get(i);
        }

        if (magA == 0 || magB == 0) return 0.0;
        return dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }
}
