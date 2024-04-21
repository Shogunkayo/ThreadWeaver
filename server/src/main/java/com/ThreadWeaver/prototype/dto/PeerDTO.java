package com.ThreadWeaver.prototype.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeerDTO {
    private String ipAddress;
    private int port;
}
