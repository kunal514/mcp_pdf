package com.example.pdfserver.service;

import com.example.pdfserver.model.VectorChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VectorStoreService {

    private final List<VectorChunk> store = new ArrayList<>();

    public void addAll(List<VectorChunk> chunks) {
        store.addAll(chunks);
    }

    public List<VectorChunk> getAllChunks() {
        return new ArrayList<>(store);
    }

    public List<Float> findMostSimilarEmbedding(List<Float> inputEmbedding) {
        double maxSimilarity = -1.0;
        List<Float> bestMatch = null;

        for (VectorChunk chunk : store) {
            double similarity = cosineSimilarity(inputEmbedding, chunk.getEmbedding());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = chunk.getEmbedding();
            }
        }

        return bestMatch;
    }

    private double cosineSimilarity(List<Float> a, List<Float> b) {
        if (a.size() != b.size()) return -1.0;

        double dot = 0.0, magA = 0.0, magB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            magA += a.get(i) * a.get(i);
            magB += b.get(i) * b.get(i);
        }

        if (magA == 0 || magB == 0) return 0;

        return dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }
}
