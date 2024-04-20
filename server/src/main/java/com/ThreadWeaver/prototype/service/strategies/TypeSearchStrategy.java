package com.ThreadWeaver.prototype.service.strategies;

import com.ThreadWeaver.prototype.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;

public class TypeSearchStrategy implements SearchStrategy {
    private final List<String> types;

    public TypeSearchStrategy(List<String> types) {
        this.types = types;
    }

    @Override
    public List<FileMetadata> search(List<FileMetadata> files) {
        List<FileMetadata> searchResults = new ArrayList<>();
        for (FileMetadata file : files) {
            if (types.contains(file.getFileType())) {
                searchResults.add(file);
            }
        }

        return searchResults;
    }
}
