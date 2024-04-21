package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.dto.PeerDTO;
import com.ThreadWeaver.prototype.model.ChunkChecksum;
import com.ThreadWeaver.prototype.model.ChunkChecksumPersist;
import com.ThreadWeaver.prototype.model.FileChunk;
import com.ThreadWeaver.prototype.model.Peer;
import com.ThreadWeaver.prototype.repository.ChunkChecksumPersistRepository;
import com.ThreadWeaver.prototype.repository.FileChunkRepository;
import com.ThreadWeaver.prototype.repository.PeerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Service
public class PeerService {
    private final PeerRepository peerRepository;
    private final FileChunkRepository fileChunkRepository;

    public PeerService(PeerRepository peerRepository, FileChunkRepository fileChunkRepository, ChunkChecksumPersistRepository chunkChecksumPersistRepository, FileChunkRepository fileChunkRepository1) {
        this.peerRepository = peerRepository;
        this.fileChunkRepository = fileChunkRepository1;
    }

    public List<PeerDTO> selectPeersForUpload(int noFileChunks) {
        List<Peer> onlinePeers = peerRepository.findOnlinePeers();
        onlinePeers.sort(Comparator.comparingInt(peer -> peer.getFileChunks().size()));

        int numPeersToSelect = Math.min(noFileChunks, onlinePeers.size());
        List<Peer> selectedPeers = onlinePeers.subList(0, numPeersToSelect);

        // Ensure we have enough online peers to match the number of file chunks
        while (selectedPeers.size() < noFileChunks) {
            selectedPeers.addAll(onlinePeers);
        }

        selectedPeers = selectedPeers.subList(0, noFileChunks);

        List<PeerDTO> selectedPeersDTO = new ArrayList<>();
        for (Peer peer : selectedPeers) {
            PeerDTO peerDTO = new PeerDTO();
            peerDTO.setIpAddress(peer.getIpAddress());
            peerDTO.setPort(peer.getPort());
            selectedPeersDTO.add(peerDTO);
        }
        return selectedPeersDTO;
    }

    public void registerPeer(Peer peer, List<ChunkChecksum> chunkChecksums) {
        // Check if the peer already exists based on IP address and port
        Optional<Peer> existingPeer = peerRepository.findByIpAddressAndPort(peer.getIpAddress(), peer.getPort());

        if (existingPeer.isPresent()) {
            // Peer already exists, update its file chunks and online status
            Peer existing = existingPeer.get();
            existing.setOnline(true);
            peerRepository.save(existing);
        } else {
            // Peer does not exist, save it along with its file chunks
            peerRepository.save(peer);
        }

        if (chunkChecksums != null) {
            for (ChunkChecksum chunkChecksum : chunkChecksums) {
                FileChunk fileChunk = new FileChunk();
                fileChunk.setChecksum(chunkChecksum.getChecksum());
                fileChunk.setPeer(peer);
                fileChunk.setChunkName(chunkChecksum.getChunkName());
                fileChunkRepository.save(fileChunk);
            }
        }
    }

    public void deregisterPeer(Long peerId) {
        Optional<Peer> optionalPeer = peerRepository.findById(peerId);
        if (optionalPeer.isPresent()) {
            Peer peer = optionalPeer.get();
            peerRepository.delete(peer);
        }
        else {
            throw new IllegalArgumentException("Peer with ID " + peerId + " not found.");
        }
    }

    public List<Peer> getOnlinePeers() {
        return peerRepository.findOnlinePeers();
    }

    public List<Peer> getAllPeers() {
        return peerRepository.findAll();
    }

    public void updatePeer(Peer peer) {
        peerRepository.save(peer);
    }

    public Optional<Peer> findByIpAddressAndPort (String ipAddress, int port) {
        return peerRepository.findByIpAddressAndPort(ipAddress, port);
    }

    public List<Peer> getPeersContainingFileChunks(List<String> fileChunkNames) {
        return peerRepository.findByFileChunksChunkNameIn(fileChunkNames);
    }

    public void savePeer(Peer peer) {
        peerRepository.save(peer);
    }
}
