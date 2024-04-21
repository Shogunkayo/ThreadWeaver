package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadataService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata saveFileMetadata(FileMetadata fileMetadata) {
        return fileMetadataRepository.save(fileMetadata);
    }

    public FileMetadata getFileMetadataById(Long fileId) {
        Optional<FileMetadata> optionalFileMetadata = fileMetadataRepository.findById(fileId);

        // Check if the FileMetadata object exists
        if (optionalFileMetadata.isPresent()) {
            return optionalFileMetadata.get();
        } else {
            throw new IllegalArgumentException("FileMetadata with ID " + fileId + " not found");
        }
    }
}
