package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.FileChunk;
import com.ThreadWeaver.prototype.repository.FileChunkRepository;
import org.springframework.stereotype.Service;

@Service
public class FileChunkService {
    private final FileChunkRepository fileChunkRepository;

    public FileChunkService(FileChunkRepository fileChunkRepository) {
        this.fileChunkRepository = fileChunkRepository;
    }

    public void saveFileChunk(FileChunk fileChunk) {
        fileChunkRepository.save(fileChunk);
    }

    public FileChunk getFileChunkByChunkNameAndChecksum(String chunkName, String checksum) {
        return fileChunkRepository.findByChunkNameAndChecksum(chunkName, checksum);
    }

}
