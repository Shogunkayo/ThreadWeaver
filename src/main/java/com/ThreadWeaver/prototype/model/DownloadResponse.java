package com.ThreadWeaver.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DownloadResponse<T> {
    private boolean isChunks;
    private T data;

    public DownloadResponse(boolean isChunks, T data) {
        this.isChunks = isChunks;
        this.data = data;
    }
}
