package com.compuslink.colocation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ColocFileStorageService {
    @Value("${app.upload.dir}") private String uploadDir;
    @Value("${app.base-url}") private String baseUrl;

    public String store(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir); Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), dir.resolve(filename));
            return baseUrl + "/coloc-uploads/" + filename;
        } catch (IOException e) { throw new RuntimeException("Failed to store file", e); }
    }
}
