package com.ThreadWeaver.prototype.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDTO {
    private byte[] chunkData;
    private String chunkName;
    private String checksum;
    private Long fileMetadataId;
}
