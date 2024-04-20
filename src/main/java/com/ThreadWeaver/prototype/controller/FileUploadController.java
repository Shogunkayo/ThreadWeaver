package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.model.FileChunk;
import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.model.Peer;
import com.ThreadWeaver.prototype.model.UploadRequest;
import com.ThreadWeaver.prototype.service.FileChunkService;
import com.ThreadWeaver.prototype.service.FileMetadataService;
import com.ThreadWeaver.prototype.service.PeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    @Autowired
    private PeerService peerService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private FileChunkService fileChunkService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateFileUpload(@RequestParam("noFileChunks") int noFileChunks) {
        try {
            List<Peer> selectedPeers = peerService.selectPeersForUpload(noFileChunks);
            return ResponseEntity.ok().body(selectedPeers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/finalize")
    public ResponseEntity<String> finalizeFileUpload(@RequestBody UploadRequest uploadRequest) {
        try {
            List<Peer> peers = uploadRequest.getPeers();
            List<FileChunk> fileChunks = uploadRequest.getFileChunks();
            FileMetadata fileMetadata = uploadRequest.getFileMetadata();

            if (peers.size() != fileChunks.size()) {
                throw new IllegalArgumentException("Number of peers does not match number of file chunks");
            }

            // Save file metadata
            FileMetadata savedFileMetadata = fileMetadataService.saveFileMetadata(fileMetadata);

            // Save file chunks with corresponding peers
            for (int i = 0; i < peers.size(); i++) {
                Peer peer = peers.get(i);
                FileChunk fileChunk = fileChunks.get(i);

                peer.getFileChunks().add(fileChunk);
                peerService.updatePeer(peer);
                fileChunkService.saveFileChunk(fileChunk);
            }

            return ResponseEntity.ok().body("File upload finalized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
