package com.example.pdfserver.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {
    public static List<String> chunk(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }
}
