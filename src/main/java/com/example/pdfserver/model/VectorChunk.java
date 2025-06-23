package com.example.pdfserver.model;

import java.util.List;

public class VectorChunk {
    private final String text;
    private final List<Float> embedding;

    public VectorChunk(String text, List<Float> embedding) {
        this.text = text;
        this.embedding = embedding;
    }

    public String getText() {
        return text;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }
}
