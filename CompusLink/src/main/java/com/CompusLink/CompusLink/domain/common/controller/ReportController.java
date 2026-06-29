package com.CompusLink.CompusLink.domain.common.controller;

import com.CompusLink.CompusLink.domain.common.dto.ReportRequest;
import com.CompusLink.CompusLink.domain.common.dto.ReportResponse;
import com.CompusLink.CompusLink.domain.common.service.ReportService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        ReportResponse response = reportService.create(request, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public List<ReportResponse> getMyReports(@AuthenticationPrincipal UserPrincipal principal) {
        return reportService.getMyReports(principal.getUser().getId());
    }
}
