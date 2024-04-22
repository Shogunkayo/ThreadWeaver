package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.dto.PeerDTO;
import com.ThreadWeaver.prototype.model.ChunkChecksumPersist;
import com.ThreadWeaver.prototype.model.DownloadResponse;
import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.model.Peer;
import com.ThreadWeaver.prototype.service.FileMetadataService;
import com.ThreadWeaver.prototype.service.PeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/download")
public class FileDownloadController {
    @Autowired
    private PeerService peerService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @PostMapping("/{fileId}")
    public ResponseEntity<?> handleDownloadRequest(@PathVariable Long fileId) {
        try {
            // Retrieve the FileMetadata object associated with the file ID
            FileMetadata fileMetadata = fileMetadataService.getFileMetadataById(fileId);

            // Check if all the peers containing the file chunks are currently online
            List<ChunkChecksumPersist> chunkChecksumPersists = fileMetadata.getChunkChecksumPersists();
            List<String> chunkNames = chunkChecksumPersists.stream()
                    .map(ChunkChecksumPersist::getChunkName)
                    .toList();
            List<Peer> peers = peerService.getPeersContainingFileChunks(chunkNames);
            boolean allPeersOnline = peers.stream().allMatch(Peer::isOnline);

            // If all peers are online, return a response with the list of online peers and file chunks
            if (allPeersOnline) {
                List<PeerDTO> peerDTOList = new ArrayList<>();
                for (Peer peer : peers) {
                    PeerDTO peerDTO = new PeerDTO();
                    peerDTO.setIpAddress(peer.getIpAddress());
                    peerDTO.setPort(peer.getPort());
                    peerDTOList.add(peerDTO);
                }
                DownloadResponse response = new DownloadResponse(true, peerDTOList, fileMetadata.getFilename());
                return ResponseEntity.ok().body(response);
            } else {
                // Check if the owner peer is online
                Optional<Peer> ownerPeer = peerService.findByIpAddressAndPort(fileMetadata.getOwnerIp(), fileMetadata.getOwnerPort());

                if (ownerPeer.isPresent() && ownerPeer.get().isOnline()) {
                    PeerDTO ownerPeerDTO = new PeerDTO();
                    ownerPeerDTO.setIpAddress(ownerPeer.get().getIpAddress());
                    ownerPeerDTO.setPort(ownerPeer.get().getPort());
                    List<PeerDTO> peerDTOList = new ArrayList<>();
                    peerDTOList.add(ownerPeerDTO);
                    DownloadResponse response = new DownloadResponse(false, peerDTOList, fileMetadata.getFilename());
                    return ResponseEntity.ok().body(response);
                }

                else {
                    return ResponseEntity.badRequest().body("File cannot be downloaded at this moment");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
