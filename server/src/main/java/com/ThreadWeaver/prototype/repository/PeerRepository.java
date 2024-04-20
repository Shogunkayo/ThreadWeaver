package com.ThreadWeaver.prototype.repository;

import com.ThreadWeaver.prototype.model.Peer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeerRepository extends JpaRepository<Peer, Long> {
    @Query("SELECT p FROM Peer p WHERE p.online = true")
    List<Peer> findOnlinePeers();

    Optional<Peer> findByIpAddressAndPort(String ipAddress, int port);

    List<Peer> findByFileChunksChunkNameIn(List<String> fileChunkNames);
}