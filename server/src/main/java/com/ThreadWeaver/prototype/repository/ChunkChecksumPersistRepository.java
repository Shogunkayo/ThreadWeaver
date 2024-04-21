package com.ThreadWeaver.prototype.repository;

import com.ThreadWeaver.prototype.model.ChunkChecksumPersist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkChecksumPersistRepository extends JpaRepository<ChunkChecksumPersist, Long> {
}
