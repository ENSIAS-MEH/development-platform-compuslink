package com.compuslink.user.service;

import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.user.dto.CvResponse;
import com.compuslink.user.model.Cv;
import com.compuslink.user.repository.CvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CvService {

    private final CvRepository cvRepo;
    private final UserFileStorageService fileStorage;

    public List<CvResponse> getMyCvs(UUID userId) {
        return cvRepo.findByUserIdOrderByUploadedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    public CvResponse upload(UUID userId, MultipartFile file) {
        boolean first = cvRepo.countByUserId(userId) == 0;
        Cv cv = Cv.builder()
                .userId(userId)
                .fileUrl(fileStorage.store(file))
                .originalName(file.getOriginalFilename())
                .isDefault(first) // first CV becomes the default automatically
                .build();
        return toResponse(cvRepo.save(cv));
    }

    public void delete(UUID userId, UUID cvId) {
        Cv cv = cvRepo.findByIdAndUserId(cvId, userId)
                .orElseThrow(() -> new EntityNotFoundException("CV not found"));
        boolean wasDefault = cv.isDefault();
        fileStorage.delete(cv.getFileUrl());
        cvRepo.delete(cv);
        // promote the most recent remaining CV to default if we removed the default one
        if (wasDefault) {
            cvRepo.findByUserIdOrderByUploadedAtDesc(userId).stream().findFirst().ifPresent(next -> {
                next.setDefault(true);
                cvRepo.save(next);
            });
        }
    }

    public CvResponse setDefault(UUID userId, UUID cvId) {
        Cv target = cvRepo.findByIdAndUserId(cvId, userId)
                .orElseThrow(() -> new EntityNotFoundException("CV not found"));
        cvRepo.findByUserIdAndIsDefaultTrue(userId).ifPresent(current -> {
            if (!current.getId().equals(cvId)) {
                current.setDefault(false);
                cvRepo.save(current);
            }
        });
        target.setDefault(true);
        return toResponse(cvRepo.save(target));
    }

    private CvResponse toResponse(Cv cv) {
        return CvResponse.builder()
                .id(cv.getId())
                .fileUrl(cv.getFileUrl())
                .originalName(cv.getOriginalName())
                .uploadedAt(cv.getUploadedAt())
                .isDefault(cv.isDefault())
                .build();
    }
}
