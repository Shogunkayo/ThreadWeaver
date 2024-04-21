package com.ThreadWeaver.prototype.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chunk_checksum")
public class ChunkChecksumPersist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chunkName;
    private String checksum;

    @ManyToOne
    @JoinColumn(name = "metadata_id")
    private FileMetadata fileMetadata;
}
