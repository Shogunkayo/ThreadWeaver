package com.ThreadWeaver.prototype.runner;

import com.ThreadWeaver.prototype.model.ChunkChecksum;
import com.ThreadWeaver.prototype.model.PeerRegistrationRequest;
import com.ThreadWeaver.prototype.service.FileService;
import com.ThreadWeaver.prototype.service.PeerRegistrationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Component
public class PeerRegistrationRunner implements ApplicationRunner {
    private final PeerRegistrationService peerRegistrationService;

    private final FileService fileService;
    private static final String CHUNKS_FOLDER_PATH = "chunks";
    private static final String FILES_FOLDER_PATH = "files";

    public PeerRegistrationRunner(PeerRegistrationService peerRegistrationService, FileService fileService) {
        this.peerRegistrationService = peerRegistrationService;
        this.fileService = fileService;
    }

    @Override
    public void run(ApplicationArguments args) throws  Exception {
        PeerRegistrationRequest peerRegistrationRequest = new PeerRegistrationRequest();
        peerRegistrationRequest.setPort(11000);
        peerRegistrationRequest.setIpAddress(InetAddress.getLocalHost().getHostAddress());
        File filesFolder = new File(FILES_FOLDER_PATH);
        if (!filesFolder.exists()) {
            boolean created = filesFolder.mkdir();
            if (created) {
                System.out.println("Created 'files' folder");
            }
            else {
                System.out.println("Failed to create 'files' folder");
            }
        }

        File chunksFolder = new File(CHUNKS_FOLDER_PATH);
        if (!chunksFolder.exists() || !chunksFolder.isDirectory()) {
            boolean created = chunksFolder.mkdir();
            if (created) {
                System.out.println("Created 'chunks' folder");
            }
            else {
                System.out.println("Failed to create 'chunks' folder");
            }
            List<ChunkChecksum> chunkChecksums = new ArrayList<>();
            peerRegistrationRequest.setChunkChecksums(chunkChecksums);
        }
        else {
            List<File> chunkFiles = new ArrayList<>();
            File[] files = chunksFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        chunkFiles.add(file);
                    }
                }
            }

            List<ChunkChecksum> chunkChecksums = new ArrayList<>();
            for (File file: chunkFiles) {
                ChunkChecksum chunkChecksum = new ChunkChecksum();
                String[] parts = file.getName().split("_");
                chunkChecksum.setChunkName(parts[0]);
                chunkChecksum.setFileMetadataId(Long.parseLong(parts[1]));
                chunkChecksum.setChecksum(fileService.calculateMD5Checksum(new FileInputStream(file)));
                chunkChecksums.add(chunkChecksum);
            }

        }

        peerRegistrationService.registerWithServer(peerRegistrationRequest)
                .subscribe(result -> System.out.println("Result: " + result),
                error -> System.err.println("Error: " + error));
    }

}
