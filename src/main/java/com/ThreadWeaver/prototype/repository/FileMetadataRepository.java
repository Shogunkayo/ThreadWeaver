package com.ThreadWeaver.prototype.repository;

import com.ThreadWeaver.prototype.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> { }
