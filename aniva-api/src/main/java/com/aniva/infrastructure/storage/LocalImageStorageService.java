package com.aniva.infrastructure.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public String upload(MultipartFile file) {

        try {

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalName = file.getOriginalFilename();

            if (originalName == null || originalName.isBlank()) {
                throw new RuntimeException("Invalid file name");
            }

            String fileName = UUID.randomUUID() + "_" + originalName.replace(" ", "_");

            Path targetPath = uploadDir.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }

    @Override
    public void delete(String imageUrl) {

        try {

            String fileName = imageUrl.replace("/uploads/", "");

            Path filePath = uploadDir.resolve(fileName).normalize();

            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}