package com.ThreadWeaver.prototype.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "f_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String description;
    private long fileSize;
    private String fileExtension;
    private String fileType;
    private String checksum;

    @ElementCollection
    private List<String> fileChunkNames;

    @ManyToOne
    @JoinColumn(name = "ownerIp", referencedColumnName = "ipAddress")
    private Peer ownerPeer;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
