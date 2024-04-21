package com.ThreadWeaver.prototype.service.strategies;

import com.ThreadWeaver.prototype.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;

public class SizeSearchStrategy implements SearchStrategy {
    private final long minSize;
    private final long maxSize;

    public SizeSearchStrategy(long minSize, long maxSize) {
        super();
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    @Override
    public List<FileMetadata> search(List<FileMetadata> files) {
        List<FileMetadata> searchResults = new ArrayList<>();
        for (FileMetadata file : files) {
            if (file.getFileSize() >= minSize && file.getFileSize() <= maxSize) {

                    searchResults.add(file);
            }
        }

        return searchResults;
    }
}
