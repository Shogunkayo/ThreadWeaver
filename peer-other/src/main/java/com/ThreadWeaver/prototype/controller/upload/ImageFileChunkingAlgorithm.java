package com.ThreadWeaver.prototype.controller.upload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class ImageFileChunkingAlgorithm implements FileChunkingAlgorithm{

    private static final int CHUNK_PIXELS = 100;
    @Override
    public List<byte[]> chunkFile(byte[] fileData) {
        List<byte[]> chunks = new ArrayList<>();
        try {
            // Read the image data
            ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            int width = image.getWidth();
            int height = image.getHeight();

            // Calculate the number of chunks based on pixels
            int totalPixels = width * height;
            int numChunks = (int) Math.ceil((double) totalPixels / CHUNK_PIXELS);

            // Chunk the image
            for (int i = 0; i < numChunks; i++) {
                int startX = (i * CHUNK_PIXELS) % width;
                int startY = (i * CHUNK_PIXELS) / width;
                int endX = Math.min(startX + CHUNK_PIXELS, width);
                int endY = Math.min(startY + CHUNK_PIXELS / width, height);

                BufferedImage chunkImage = image.getSubimage(startX, startY, endX - startX, endY - startY);
                chunks.add(imageToBytes(chunkImage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunks;
    }

    private byte[] imageToBytes(BufferedImage chunkImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(chunkImage, "png", baos);
        return baos.toByteArray();
    }

}
