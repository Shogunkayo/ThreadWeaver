package com.ThreadWeaver.prototype.controller.upload;

import java.util.ArrayList;
import java.util.List;

public class DefaultFileChunkingAlgorithm implements FileChunkingAlgorithm {

    private static final int CHUNK_SIZE_BYTES = 1024 * 1024; // 1 MB
    @Override
    public List<byte[]> chunkFile(byte[] fileData) {
        List<byte[]> chunks = new ArrayList<>();
        int offset = 0;
        while (offset < fileData.length) {
            int length = Math.min(CHUNK_SIZE_BYTES, fileData.length - offset);
            byte[] chunk = new byte[length];
            System.arraycopy(fileData, offset, chunk, 0, length);
            chunks.add(chunk);
            offset += length;
        }
        return chunks;
    }
}
