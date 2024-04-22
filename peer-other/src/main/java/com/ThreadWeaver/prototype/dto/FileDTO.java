package com.ThreadWeaver.prototype.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private String checksum;
    private Long fileMetadataId;
    private byte[] data;
}
