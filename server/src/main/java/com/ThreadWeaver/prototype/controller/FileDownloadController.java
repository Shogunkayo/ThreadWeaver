package com.ThreadWeaver.prototype.controller;

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

import java.util.List;

@RestController
@RequestMapping("/api/download")
public class FileDownloadController {
    @Autowired
    private PeerService peerService;

    @Autowired
    private FileMetadataService fileMetadataService;

    /*
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
                DownloadResponse<Object> response = new DownloadResponse<>(true, peers);
                return ResponseEntity.ok().body(response);
            } else {
                // Check if the owner peer is online
                boolean ownerPeerOnline = fileMetadata.getOwnerPeer().isOnline();

                if (ownerPeerOnline) {
                    DownloadResponse<Object> response = new DownloadResponse<>(false, fileMetadata.getOwnerPeer());
                    return ResponseEntity.ok().body(response);
                }

                else {
                    return ResponseEntity.badRequest().body("Not all peers containing the file chunks are online.");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    */

}
