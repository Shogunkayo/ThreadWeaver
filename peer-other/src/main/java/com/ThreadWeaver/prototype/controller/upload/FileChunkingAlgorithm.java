package com.ThreadWeaver.prototype.controller.upload;

import java.util.List;

public interface FileChunkingAlgorithm {
    List<byte[]> chunkFile(byte[] fileData);
}
