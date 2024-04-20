package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.FileChunk;
import com.ThreadWeaver.prototype.repository.FileChunkRepository;
import org.springframework.stereotype.Service;

@Service
public class FileChunkService {
    private FileChunkRepository fileChunkRepository;
    public void saveFileChunk(FileChunk fileChunk) {
        fileChunkRepository.save(fileChunk);
    }
}
