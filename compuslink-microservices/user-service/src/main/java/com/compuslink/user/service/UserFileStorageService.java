package com.compuslink.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UserFileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String store(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), dir.resolve(filename));
            return baseUrl + "/user-uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public void delete(String url) {
        if (url == null) return;
        try {
            String filename = url.substring(url.lastIndexOf("/") + 1);
            Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException e) {
            // log and ignore
        }
    }
}
