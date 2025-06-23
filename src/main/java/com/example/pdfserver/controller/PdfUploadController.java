package com.example.pdfserver.controller;


import com.example.pdfserver.model.VectorChunk;
import com.example.pdfserver.service.OllamaEmbeddingClient;
import com.example.pdfserver.service.VectorStoreService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class PdfUploadController {

    @Autowired
    private OllamaEmbeddingClient embeddingClient;

    @Autowired
    private VectorStoreService vectorStore;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Read PDF text
            InputStream inputStream = file.getInputStream();
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            if (!StringUtils.hasText(text)) {
                return ResponseEntity.badRequest().body("Empty PDF file");
            }

            // Split text into chunks
            List<String> chunks = splitTextIntoChunks(text, 300); // 300-character chunks

            List<VectorChunk> vectorChunks = new ArrayList<>();

            for (String chunk : chunks) {
                List<Float> embedding = embeddingClient.getEmbedding(chunk);
                if (embedding == null || embedding.isEmpty()) continue;

                VectorChunk vectorChunk = new VectorChunk(chunk, embedding);
                vectorChunks.add(vectorChunk);
            }

            vectorStore.addAll(vectorChunks);

            return ResponseEntity.ok("PDF processed and embeddings stored successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing PDF: " + e.getMessage());
        }
    }

    private List<String> splitTextIntoChunks(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(length, start + maxLength);
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }
}
