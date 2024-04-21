package com.ThreadWeaver.prototype.service;


import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.repository.FileMetadataRepository;
import com.ThreadWeaver.prototype.service.strategies.SearchStrategy;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
@Setter
public class SearchService {
    private final FileMetadataRepository fileMetadataRepository;
    private SearchStrategy strategy;

    public SearchService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public List<FileMetadata> searchFiles() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set");
        }
        List<FileMetadata> files = fileMetadataRepository.findAll();
        return strategy.search(files);
    }
}
