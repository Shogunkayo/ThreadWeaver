package com.ThreadWeaver.prototype.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private Long id;
    private String filename;
    private String description;
    private long fileSize;
    private String fileExtension;
    private String fileType;
    private String checksum;
}