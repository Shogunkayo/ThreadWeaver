package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.FileChunk;
import com.ThreadWeaver.prototype.model.Peer;
import com.ThreadWeaver.prototype.repository.FileChunkRepository;
import com.ThreadWeaver.prototype.repository.PeerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Service
public class PeerService {
    private PeerRepository peerRepository;
    private FileChunkRepository fileChunkRepository;
    public List<Peer> selectPeersForUpload(int noFileChunks) {
        List<Peer> onlinePeers = peerRepository.findOnlinePeers();
        onlinePeers.sort(Comparator.comparingInt(peer -> peer.getFileChunks().size()));

        int numPeersToSelect = Math.min(noFileChunks, onlinePeers.size());
        List<Peer> selectedPeers = onlinePeers.subList(0, numPeersToSelect);

        // Ensure we have enough online peers to match the number of file chunks
        while (selectedPeers.size() < noFileChunks) {
            selectedPeers.addAll(onlinePeers);
        }

        selectedPeers = selectedPeers.subList(0, noFileChunks);
        return selectedPeers;
    }

    public void registerPeer(Peer peer, List<FileChunk> fileChunks) {
        // Check if the peer already exists based on IP address and port
        Optional<Peer> existingPeer = peerRepository.findByIpAddressAndPort(peer.getIpAddress(), peer.getPort());

        if (existingPeer.isPresent()) {
            // Peer already exists, update its file chunks
            Peer existing = existingPeer.get();
            existing.getFileChunks().addAll(fileChunks);
            peerRepository.save(existing);
        } else {
            // Peer does not exist, save it along with its file chunks
            peer.setFileChunks(fileChunks);
            peerRepository.save(peer);
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

    public List<Peer> getPeersContainingFileChunks(List<String> fileChunkNames) {
        return peerRepository.findByFileChunksChunkNameIn(fileChunkNames);
    }

    public void savePeer(Peer peer) {
        peerRepository.save(peer);
    }
}
