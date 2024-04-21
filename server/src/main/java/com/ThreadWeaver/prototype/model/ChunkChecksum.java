package com.ThreadWeaver.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkChecksum {
    private String chunkName;
    private String checksum;
    private Long fileMetadataId;
}
