package com.ThreadWeaver.prototype.service.strategies;

import com.ThreadWeaver.prototype.model.FileMetadata;
import java.util.List;

public interface SearchStrategy {
    List<FileMetadata> search(List<FileMetadata> files);
}
