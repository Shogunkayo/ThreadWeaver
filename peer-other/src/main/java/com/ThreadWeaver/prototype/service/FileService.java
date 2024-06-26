package com.ThreadWeaver.prototype.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileService {
    public String calculateMD5Checksum(InputStream is) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (DigestInputStream dis = new DigestInputStream(is, md))
        {
            /* Read decorated stream (dis) to EOF as normal... */
        }

        byte[] digest = md.digest();

        // Convert byte array to hexadecimal string
        BigInteger bigInt = new BigInteger(1, digest);
        String checksum = bigInt.toString(16);

        // Add leading zeros to ensure proper length
        checksum = String.format("%32s", checksum).replace(' ', '0');

        return checksum;
    }

    public String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null; // No extension found
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    public String getFileTypeFromExtension(String fileExtension) {
        if (fileExtension == null) {
            return "Unknown";
        }
        return switch (fileExtension.toLowerCase()) {
            case "txt", "csv", "log" -> "text";
            case "jpg", "jpeg", "png", "gif" -> "image";
            default -> "Unknown";
        };
    }
}
