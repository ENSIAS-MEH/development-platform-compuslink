package com.compuslink.user.service;

import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.user.dto.CvResponse;
import com.compuslink.user.model.Cv;
import com.compuslink.user.repository.CvRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvServiceTest {

    @Mock
    private CvRepository cvRepository;

    @Mock
    private UserFileStorageService fileStorageService;

    @InjectMocks
    private CvService cvService;

    private UUID testUserId;
    private UUID testCvId;
    private Cv testCv;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testCvId = UUID.randomUUID();

        testCv = new Cv();
        testCv.setId(testCvId);
        testCv.setUserId(testUserId);
        testCv.setFileUrl("http://localhost/user-uploads/cv123.pdf");
        testCv.setOriginalName("cv.pdf");
        testCv.setDefault(true);
        testCv.setUploadedAt(OffsetDateTime.now());
    }

    @Test
    void testGetMyCvsSuccess() {
        Cv cv1 = new Cv();
        cv1.setId(UUID.randomUUID());
        cv1.setUserId(testUserId);
        cv1.setOriginalName("cv1.pdf");

        Cv cv2 = new Cv();
        cv2.setId(UUID.randomUUID());
        cv2.setUserId(testUserId);
        cv2.setOriginalName("cv2.pdf");

        when(cvRepository.findByUserIdOrderByUploadedAtDesc(testUserId))
                .thenReturn(List.of(cv1, cv2));

        List<CvResponse> responses = cvService.getMyCvs(testUserId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(cvRepository).findByUserIdOrderByUploadedAtDesc(testUserId);
    }

    @Test
    void testGetMyCvsEmpty() {
        when(cvRepository.findByUserIdOrderByUploadedAtDesc(testUserId))
                .thenReturn(List.of());

        List<CvResponse> responses = cvService.getMyCvs(testUserId);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void testUploadFirstCvSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("cv.pdf");
        when(cvRepository.countByUserId(testUserId)).thenReturn(0L);
        when(fileStorageService.store(file)).thenReturn("http://localhost/user-uploads/cv123.pdf");
        when(cvRepository.save(any(Cv.class))).thenReturn(testCv);

        CvResponse response = cvService.upload(testUserId, file);

        assertNotNull(response);
        assertTrue(response.isDefault());
        verify(cvRepository).save(any(Cv.class));
    }

    @Test
    void testUploadSecondCvNotDefault() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("cv.pdf");
        Cv existingCv = new Cv();
        existingCv.setUserId(testUserId);

        when(cvRepository.countByUserId(testUserId)).thenReturn(1L);
        when(fileStorageService.store(file)).thenReturn("http://localhost/user-uploads/cv456.pdf");

        Cv newCv = new Cv();
        newCv.setDefault(false);
        when(cvRepository.save(any(Cv.class))).thenReturn(newCv);

        CvResponse response = cvService.upload(testUserId, file);

        assertNotNull(response);
        assertFalse(response.isDefault());
        verify(cvRepository).save(any(Cv.class));
    }

    @Test
    void testDeleteCvSuccess() {
        testCv.setDefault(false);
        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.of(testCv));

        cvService.delete(testUserId, testCvId);

        verify(fileStorageService).delete(testCv.getFileUrl());
        verify(cvRepository).delete(testCv);
    }

    @Test
    void testDeleteDefaultCvPromotesNext() {
        Cv defaultCv = new Cv();
        defaultCv.setId(testCvId);
        defaultCv.setUserId(testUserId);
        defaultCv.setDefault(true);
        defaultCv.setFileUrl("http://localhost/user-uploads/default.pdf");

        Cv nextCv = new Cv();
        nextCv.setId(UUID.randomUUID());
        nextCv.setUserId(testUserId);
        nextCv.setDefault(false);

        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.of(defaultCv));
        when(cvRepository.findByUserIdOrderByUploadedAtDesc(testUserId)).thenReturn(List.of(nextCv));

        cvService.delete(testUserId, testCvId);

        verify(cvRepository).delete(defaultCv);
        verify(cvRepository).save(nextCv);
        assertTrue(nextCv.isDefault());
    }

    @Test
    void testDeleteCvNotFound() {
        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cvService.delete(testUserId, testCvId));
        verify(cvRepository, never()).delete(any());
    }

    @Test
    void testSetDefaultCvSuccess() {
        Cv currentDefault = new Cv();
        currentDefault.setId(UUID.randomUUID());
        currentDefault.setDefault(true);

        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.of(testCv));
        when(cvRepository.findByUserIdAndIsDefaultTrue(testUserId)).thenReturn(Optional.of(currentDefault));
        when(cvRepository.save(any(Cv.class))).thenReturn(testCv);

        CvResponse response = cvService.setDefault(testUserId, testCvId);

        assertNotNull(response);
        assertTrue(response.isDefault());
        assertFalse(currentDefault.isDefault());
        verify(cvRepository, times(2)).save(any(Cv.class));
    }

    @Test
    void testSetDefaultCvNotFound() {
        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cvService.setDefault(testUserId, testCvId));
    }

    @Test
    void testSetDefaultCvNoCurrentDefault() {
        when(cvRepository.findByIdAndUserId(testCvId, testUserId)).thenReturn(Optional.of(testCv));
        when(cvRepository.findByUserIdAndIsDefaultTrue(testUserId)).thenReturn(Optional.empty());
        when(cvRepository.save(any(Cv.class))).thenReturn(testCv);

        CvResponse response = cvService.setDefault(testUserId, testCvId);

        assertNotNull(response);
        verify(cvRepository, times(1)).save(any(Cv.class));
    }
}
