package com.ThreadWeaver.prototype.service.strategies;

import com.ThreadWeaver.prototype.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;

public class KeywordSearchStrategy implements SearchStrategy {
    private final String keyword;

    public KeywordSearchStrategy(String keyword) {
        super();
        this.keyword = keyword;
    }
    @Override
    public List<FileMetadata> search(List<FileMetadata> files) {
        List<FileMetadata> searchResults = new ArrayList<>();
        for (FileMetadata file : files) {
            if (file.getFilename().contains(keyword) || file.getDescription().contains(keyword)) {
                searchResults.add(file);
            }
        }

        return searchResults;
    }
}
