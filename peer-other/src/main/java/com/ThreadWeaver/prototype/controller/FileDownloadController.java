package com.ThreadWeaver.prototype.controller;


import com.ThreadWeaver.prototype.dto.ChunkDTO;
import com.ThreadWeaver.prototype.dto.FileDTO;
import com.ThreadWeaver.prototype.dto.PeerDTO;
import com.ThreadWeaver.prototype.model.DownloadResponse;
import com.ThreadWeaver.prototype.service.FileService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Controller
public class FileDownloadController {
    @Autowired
    private FileService fileService;

    private final WebClient webClient;

    public FileDownloadController(FileService fileService, WebClient.Builder webClientBuilder) {
        this.fileService = fileService;
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080").build();
    }

    @PostMapping("/download")
    public String handleFileDownload(@RequestParam("fileId") String fileId, Model model) {
        if (fileId != null) {
            Mono<DownloadResponse> responseMono = webClient.post()
                    .uri("/api/download/{fileId}", fileId)
                    .retrieve()
                    .bodyToMono(DownloadResponse.class);

            responseMono.subscribe(
                    response -> {
                        if (response.isChunks()) {
                            List<ChunkDTO> chunkDTOList = new ArrayList<>();
                            for (PeerDTO peerDTO : response.getData()) {
                                String baseUrl = "http://" + peerDTO.getIpAddress() + ":" + peerDTO.getPort();
                                WebClient peerWebClient = WebClient.builder().baseUrl(baseUrl).build();

                                Mono<ResponseEntity<List<ChunkDTO>>> peerResponseMono = peerWebClient.post()
                                        .uri("/getChunks")
                                        .bodyValue(fileId)
                                        .retrieve()
                                        .toEntityList(ChunkDTO.class);

                                peerResponseMono.subscribe(
                                        peerResponse -> {
                                            System.out.println(peerResponse.getBody());
                                        },
                                        error -> {
                                            System.err.println("Error: " + error.getMessage());
                                        }
                                );

                            }
                        }
                        else {
                            PeerDTO ownerPeerDTO = response.getData().get(0);
                            String baseUrl = "http://" + ownerPeerDTO.getIpAddress() + ":" + ownerPeerDTO.getPort();
                            WebClient peerWebClient = WebClient.builder().baseUrl(baseUrl).build();

                            Mono<ResponseEntity<FileDTO>> ownerResponseMono = peerWebClient.post()
                                    .uri("/getOwnerFile")
                                    .bodyValue(response.getFilename())
                                    .retrieve()
                                    .toEntity(FileDTO.class);

                            ownerResponseMono.subscribe(
                                    ownerResponse -> {
                                        FileDTO fileDTO = ownerResponse.getBody();
                                        if (fileDTO != null) {
                                            System.out.println("Checksum: " + fileDTO.getChecksum());
                                            Path directory = Paths.get("files");
                                            String filename = "peerfile_" + response.getFilename();
                                            Path filePath = directory.resolve(filename);
                                            try {
                                                Files.write(filePath, fileDTO.getData());
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            System.out.println("FileDTO is null");
                                        }
                                    },
                                    error -> {
                                        System.err.println("Error: " + error.getMessage());
                                    }
                            );
                        }
                    },
                    error -> {
                        System.err.println("Error: " + error);
                    }
            );
        }
        else {
            model.addAttribute("message", "fileId field is empty: ");
        }

        return "redirect:/";
    }

    @PostMapping("/getOwnerFile")
    public ResponseEntity<?> getOwnerFile(@RequestBody String filename) {
        try {
            String filePath = "files/" + filename;

            // Check if file exists
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return ResponseEntity.badRequest().body("File does not exist");
            }

            InputStream is = new FileInputStream(filePath);
            byte[] data = Files.readAllBytes(path);

            FileDTO fileDTO = new FileDTO();
            fileDTO.setChecksum(fileService.calculateMD5Checksum(is));
            fileDTO.setData(data);

            return ResponseEntity.ok()
                    .body(fileDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving file: " + e.getMessage());
        }
    }

    @PostMapping("/getChunks")
    public ResponseEntity<?> getChunks(@RequestBody String fileId) {
        try {
            List<ChunkDTO> chunkDTOList = new ArrayList<>();
            String directoryPath = "chunks/";

            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String[] fileNameParts = filePath.getFileName().toString().split("_");
                            // Check if last part of filename matches fileId
                            if (fileNameParts.length > 0 && fileNameParts[fileNameParts.length - 1].equals(fileId)) {
                                byte[] data = Files.readAllBytes(filePath);

                                ChunkDTO chunkDTO = new ChunkDTO();
                                chunkDTO.setChunkName(filePath.getFileName().toString());
                                chunkDTO.setChunkData(data);
                                chunkDTO.setFileMetadataId(Long.valueOf(fileId));
                                chunkDTO.setChecksum(fileService.calculateMD5Checksum(new ByteArrayInputStream(data)));

                                chunkDTOList.add(chunkDTO);
                            }
                        } catch (Exception e) {
                            System.err.println("Error reading chunk: " + e.getMessage());
                        }
                    });

            return ResponseEntity.ok()
                    .body(chunkDTOList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving chunk: " + e.getMessage());
        }
    }
}
