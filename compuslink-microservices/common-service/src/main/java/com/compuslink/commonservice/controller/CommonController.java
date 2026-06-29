package com.compuslink.commonservice.controller;

import com.compuslink.commonservice.dto.*;
import com.compuslink.commonservice.model.TargetType;
import com.compuslink.commonservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController @RequiredArgsConstructor
public class CommonController {
    private final ReportService reportService;
    private final SavedItemService savedItemService;

    @PostMapping("/api/reports")
    public ResponseEntity<ReportResponse> report(@RequestBody ReportRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(reportService.report(req, userId), HttpStatus.CREATED);
    }

    @PostMapping("/api/saved")
    public ResponseEntity<SavedItemResponse> save(@RequestBody SaveRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(savedItemService.save(req, userId), HttpStatus.CREATED);
    }

    @DeleteMapping("/api/saved")
    public ResponseEntity<Void> unsave(@RequestParam TargetType targetType, @RequestParam UUID targetId, @RequestHeader("X-User-Id") UUID userId) {
        savedItemService.unsave(userId, targetType, targetId); return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/saved")
    public List<SavedItemResponse> mySaved(@RequestParam(required = false) TargetType targetType, @RequestHeader("X-User-Id") UUID userId) {
        return savedItemService.getMySaved(userId, targetType);
    }
}
