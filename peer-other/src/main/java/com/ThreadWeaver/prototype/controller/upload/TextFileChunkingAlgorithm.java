package com.ThreadWeaver.prototype.controller.upload;

import java.util.ArrayList;
import java.util.List;

public class TextFileChunkingAlgorithm implements FileChunkingAlgorithm {
    private static final String CHUNK_DELIMITER = "\n";
    @Override
    public List<byte[]> chunkFile(byte[] fileData) {
        List<byte[]> chunks = new ArrayList<>();
        String fileContent = new String(fileData);
        String[] parts = fileContent.split(CHUNK_DELIMITER);
        for (String part : parts) {
            chunks.add(part.getBytes());
        }
        return chunks;
    }
}
