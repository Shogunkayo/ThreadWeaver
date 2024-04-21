package com.ThreadWeaver.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerRegistrationRequest {
    private String ipAddress;
    private int port;
    private List<ChunkChecksum> chunkChecksums;
}