package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.dto.FileMetadataDTO;
import com.ThreadWeaver.prototype.dto.PeerDTO;
import com.ThreadWeaver.prototype.model.*;
import com.ThreadWeaver.prototype.repository.FileMetadataRepository;
import com.ThreadWeaver.prototype.service.FileChunkService;
import com.ThreadWeaver.prototype.service.FileMetadataService;
import com.ThreadWeaver.prototype.service.PeerService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    @Autowired
    private PeerService peerService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private FileChunkService fileChunkService;

    @Autowired
    private EntityManager entityManager;

    private final FileMetadataRepository fileMetadataRepository;

    public FileUploadController(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @PostMapping("/initiate")
    @Transactional
    public ResponseEntity<?> initiateFileUpload(@RequestParam("noFileChunks") int noFileChunks) {
        try {
            List<PeerDTO> selectedPeers = peerService.selectPeersForUpload(noFileChunks);

            Long fileMetadataId = getNewFileMetadataId();

            Map<String, Object> response = new HashMap<>();
            response.put("selectedPeers", selectedPeers);
            response.put("fileMetadataId", fileMetadataId);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    public Long getNewFileMetadataId() {
        FileMetadata fileMetadata = new FileMetadata(); // Create a new transient instance
        entityManager.persist(fileMetadata); // Persist the entity
        entityManager.flush(); // Flush changes to ensure ID generation
        return fileMetadata.getId(); // Return the generated ID
    }

    @PostMapping("/finalize")
    public ResponseEntity<String> finalizeFileUpload(@RequestBody FileMetadataDTO fileMetadataDTO) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFileExtension(fileMetadataDTO.getFileExtension());
        fileMetadata.setFileSize(fileMetadataDTO.getFileSize());
        fileMetadata.setFileType(fileMetadataDTO.getFileType());
        fileMetadata.setId(fileMetadataDTO.getId());
        fileMetadata.setChecksum(fileMetadataDTO.getChecksum());
        fileMetadata.setDescription(fileMetadataDTO.getDescription());
        fileMetadata.setFilename(fileMetadataDTO.getFilename());
        fileMetadata.setOwnerIp(fileMetadataDTO.getOwnerIp());
        fileMetadata.setOwnerPort(fileMetadataDTO.getOwnerPort());
        fileMetadata.setCreatedOn(LocalDateTime.now());

        List<ChunkChecksumPersist> chunkChecksumPersistList = new ArrayList<>();
        for (int i = 0; i < fileMetadataDTO.getChunkChecksumList().size(); i++) {
            ChunkChecksumPersist chunkChecksumPersist = new ChunkChecksumPersist();
            chunkChecksumPersist.setChunkName(fileMetadataDTO.getChunkChecksumList().get(i).getChunkName());
            chunkChecksumPersist.setChecksum(fileMetadataDTO.getChunkChecksumList().get(i).getChecksum());

            Optional<FileMetadata> fileMetadata_ = fileMetadataRepository.findById(fileMetadataDTO.getId());
            fileMetadata_.ifPresent(chunkChecksumPersist::setFileMetadata);

            chunkChecksumPersistList.add(chunkChecksumPersist);
        }

        for (int i = 0; i < fileMetadataDTO.getChunkChecksumList().size(); i++) {
            PeerDTO peerDTO = fileMetadataDTO.getPeerDTOList().get(i);
            Optional <Peer> peer = peerService.findByIpAddressAndPort(peerDTO.getIpAddress(), peerDTO.getPort());

            if (peer.isPresent()) {
                List<FileChunk> fileChunkList = peer.get().getFileChunks();
                FileChunk fileChunk = new FileChunk();
                fileChunk.setChunkName(fileMetadataDTO.getChunkChecksumList().get(i).getChunkName());
                fileChunk.setChecksum(fileMetadataDTO.getChunkChecksumList().get(i).getChecksum());
                fileChunk.setPeer(peer.get());
                fileChunkList.add(fileChunk);
                peer.get().setFileChunks(fileChunkList);
                peerService.savePeer(peer.get());
            }
        }

        fileMetadata.setChunkChecksumPersists(chunkChecksumPersistList);

        fileMetadataRepository.save(fileMetadata);

        return ResponseEntity.ok().body("File upload finalized successfully");
    }

}
