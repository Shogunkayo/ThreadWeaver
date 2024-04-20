package com.ThreadWeaver.prototype.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    private List<Peer> peers;
    private List<FileChunk> fileChunks;
    private FileMetadata fileMetadata;
}
