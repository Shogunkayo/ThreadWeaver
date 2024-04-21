package com.ThreadWeaver.prototype.dto;

import com.ThreadWeaver.prototype.model.ChunkChecksum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDTO {
    private Long id;
    private String filename;
    private String description;
    private long fileSize;
    private String fileExtension;
    private String fileType;
    private String checksum;
    private String ownerIp;
    private int ownerPort;
    private List<ChunkChecksum> chunkChecksumList;
    private List<PeerDTO> peerDTOList;
}
