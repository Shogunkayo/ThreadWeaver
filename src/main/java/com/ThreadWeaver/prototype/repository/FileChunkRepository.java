package com.ThreadWeaver.prototype.repository;

import com.ThreadWeaver.prototype.model.FileChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunk, Long> { }