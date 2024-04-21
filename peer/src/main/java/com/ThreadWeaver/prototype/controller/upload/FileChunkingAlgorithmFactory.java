package com.ThreadWeaver.prototype.controller.upload;

import org.springframework.stereotype.Component;

@Component
public class FileChunkingAlgorithmFactory {
    public FileChunkingAlgorithm createChunkingAlgorithm(String fileType) {
        return switch (fileType) {
            case "image" -> new ImageFileChunkingAlgorithm();
            case "text" -> new TextFileChunkingAlgorithm();
            default -> new DefaultFileChunkingAlgorithm();
        };
    }
}
