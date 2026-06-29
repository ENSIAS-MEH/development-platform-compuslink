package com.CompusLink.CompusLink.domain.marketplace.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String store(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String filename = UUID.randomUUID() + "_" + original;
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file: " + original, e);
        }
        return baseUrl + "/uploads/" + filename;
    }

    public void delete(String fileUrl) {
        String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException ignored) {
        }
    }
}
