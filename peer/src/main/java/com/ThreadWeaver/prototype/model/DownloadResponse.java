package com.ThreadWeaver.prototype.model;

import lombok.Data;

@Data
public class DownloadResponse<T> {
    private boolean isChunks;
    private T data;

    public DownloadResponse(boolean isChunks, T data) {
        this.isChunks = isChunks;
        this.data = data;
    }
}
