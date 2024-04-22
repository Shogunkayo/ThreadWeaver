package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.controller.upload.FileChunkingAlgorithm;
import com.ThreadWeaver.prototype.controller.upload.FileChunkingAlgorithmFactory;
import com.ThreadWeaver.prototype.dto.ChunkDTO;
import com.ThreadWeaver.prototype.dto.FileMetadataDTO;
import com.ThreadWeaver.prototype.dto.PeerDTO;
import com.ThreadWeaver.prototype.model.ChunkChecksum;
import com.ThreadWeaver.prototype.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FileUploadController {

    private final FileChunkingAlgorithmFactory fileChunkingAlgorithmFactory;

    private final FileService fileService;
    private final WebClient webClient;

    @Autowired
    Environment environment;

    public FileUploadController(FileChunkingAlgorithmFactory fileChunkingAlgorithmFactory, FileService fileService, WebClient.Builder webClientBuilder) {
        this.fileChunkingAlgorithmFactory = fileChunkingAlgorithmFactory;
        this.fileService = fileService;
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080").build();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("description") String description, Model model) {
        FileChunkingAlgorithm chunkingAlgorithm = null;
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image")) {
                System.out.println("IMAGE");
                chunkingAlgorithm = fileChunkingAlgorithmFactory.createChunkingAlgorithm("image");
            } else if (contentType.startsWith("text")) {
                chunkingAlgorithm = fileChunkingAlgorithmFactory.createChunkingAlgorithm("text");
            } else {
                chunkingAlgorithm = fileChunkingAlgorithmFactory.createChunkingAlgorithm("default");
            }
        }

        if (chunkingAlgorithm != null) {
            try {
                byte[] fileData = file.getBytes();
                List<byte[]> chunks = chunkingAlgorithm.chunkFile(fileData);

                // Get the list of peers
                int noChunks = chunks.size();

                Mono<Map<String, Object>> responseMono = webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/api/upload/initiate")
                                .queryParam("noFileChunks", noChunks)
                                .build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

                responseMono.subscribe(
                        response -> {
                            List<Map<String, Object>> peerDTOList = (List<Map<String, Object>>) response.get("selectedPeers");
                            Long fileMetadataId;
                            try {
                                fileMetadataId = (Long) response.get("fileMetadataId");
                            } catch (Exception e){
                                Integer int_id = (Integer) response.get("fileMetadataId");
                                fileMetadataId = int_id.longValue();
                            }
                            System.out.println("Peer DTO List: " + peerDTOList);
                            System.out.println("IP: " + peerDTOList.get(0).get("ipAddress"));
                            System.out.println("File ID: " + fileMetadataId);

                            List<ChunkChecksum> chunkChecksumList = new ArrayList<>();
                            List<PeerDTO> peerDTOlist = new ArrayList<>();

                            for (int i = 0; i < peerDTOList.size(); i++) {
                                byte[] chunkData = chunks.get(i);

                                ChunkDTO chunkDTO = new ChunkDTO();
                                chunkDTO.setChunkData(chunkData);
                                chunkDTO.setFileMetadataId(fileMetadataId);

                                InputStream is = new ByteArrayInputStream(chunkData);
                                try {
                                    chunkDTO.setChecksum(fileService.calculateMD5Checksum(is));
                                } catch (IOException | NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }

                                long timestamp = System.currentTimeMillis();
                                chunkDTO.setChunkName("chunk-" + i + "-" + timestamp + "_" + fileMetadataId);

                                ChunkChecksum chunkChecksum = new ChunkChecksum();
                                chunkChecksum.setFileMetadataId(fileMetadataId);
                                chunkChecksum.setChunkName(chunkDTO.getChunkName());
                                chunkChecksum.setChecksum(chunkDTO.getChecksum());
                                chunkChecksumList.add(chunkChecksum);

                                PeerDTO peerDTO = new PeerDTO();
                                peerDTO.setPort((Integer) peerDTOList.get(i).get("port"));
                                peerDTO.setIpAddress((String) peerDTOList.get(i).get("ipAddress"));
                                peerDTOlist.add(peerDTO);

                                String baseUrl = "http://" + peerDTOList.get(i).get("ipAddress") + ":" + peerDTOList.get(i).get("port");
                                WebClient peerWebClient = WebClient.builder().baseUrl(baseUrl).build();

                                int finalI = i;
                                peerWebClient.post()
                                        .uri( "/receiveFile")
                                        .body(BodyInserters.fromValue(chunkDTO))
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .subscribe(peerResponse -> {
                                            // Handle successful response
                                            System.out.println("Chunk sent to peer: " + peerDTOList.get(finalI).get("port"));
                                        }, error -> {
                                            // Handle error
                                            System.err.println("Error sending chunk to peer: " + peerDTOList.get(finalI).get("port") + " Error: " + error);
                                        });

                            }

                            System.out.println("FILE SIZE: " + file.getSize());

                            FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
                            fileMetadataDTO.setId(fileMetadataId);
                            fileMetadataDTO.setFilename(file.getOriginalFilename());
                            fileMetadataDTO.setDescription(description);
                            fileMetadataDTO.setFileSize(file.getSize());
                            String fileExtension  = fileService.getFileExtension(file.getOriginalFilename());
                            fileMetadataDTO.setFileExtension(fileExtension);
                            fileMetadataDTO.setFileType(fileService.getFileTypeFromExtension(fileExtension));

                            InputStream is = new ByteArrayInputStream(fileData);
                            try {
                                fileMetadataDTO.setChecksum(fileService.calculateMD5Checksum(is));
                            } catch (IOException | NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                fileMetadataDTO.setOwnerIp(InetAddress.getLocalHost().getHostAddress());
                            } catch (UnknownHostException e) {
                                throw new RuntimeException(e);
                            }

                            String port = environment.getProperty("local.server.port");
                            assert port != null;
                            fileMetadataDTO.setOwnerPort(Integer.parseInt(port));

                            fileMetadataDTO.setChunkChecksumList(chunkChecksumList);
                            fileMetadataDTO.setPeerDTOList(peerDTOlist);

                            webClient.post()
                                    .uri("/api/upload/finalize")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(fileMetadataDTO))
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .subscribe(finalResponse -> {
                                        // Handle successful response
                                        System.out.println("File metadata sent successfully");
                                    }, error -> {
                                        // Handle error
                                        System.err.println("Error sending file metadata: " + error.getMessage());
                                    });
                        },
                        error -> {
                            System.err.println("Error: " + error);
                        }
                );

                model.addAttribute("message", "File uploaded and chunked successfully: " + file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "Error uploading and chunking file: " + e.getMessage());
            }
        } else {
            model.addAttribute("message", "Error: Unsupported file type");
        }


        model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        return "redirect:/";
    }

    @PostMapping("/receiveFile")
    public ResponseEntity<?> receiveFile(@RequestBody ChunkDTO chunkDTO) {
        try {
            System.out.println(chunkDTO);

            byte[] chunkData = chunkDTO.getChunkData();
            String chunkFileName = chunkDTO.getChunkName();
            Path directory = Paths.get("chunks");
            Path chunkFilePath = directory.resolve(chunkFileName);
            Files.write(chunkFilePath, chunkData);

            return ResponseEntity.ok().body("Chunk received successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error receiving chunk: " + e.getMessage());
        }
    }

}
