package com.ThreadWeaver.prototype.model;

import com.ThreadWeaver.prototype.dto.PeerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponse {
    private boolean isChunks;
    private List<PeerDTO> data;
    private String filename;
}
