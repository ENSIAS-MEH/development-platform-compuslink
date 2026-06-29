package com.CompusLink.CompusLink.domain.common.service;

import com.CompusLink.CompusLink.domain.common.dto.ReportRequest;
import com.CompusLink.CompusLink.domain.common.dto.ReportResponse;
import com.CompusLink.CompusLink.domain.common.model.Report;
import com.CompusLink.CompusLink.domain.common.repository.ReportRepository;
import com.CompusLink.CompusLink.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepo;
    private final TargetValidationService targetValidation;

    public ReportService(ReportRepository reportRepo, TargetValidationService targetValidation) {
        this.reportRepo = reportRepo;
        this.targetValidation = targetValidation;
    }

    public ReportResponse create(ReportRequest request, UUID reporterId) {
        targetValidation.validate(request.getTargetType(), request.getTargetId());

        if (reportRepo.existsByReporterIdAndTargetTypeAndTargetId(reporterId, request.getTargetType(), request.getTargetId())) {
            throw new DuplicateResourceException("You have already reported this content");
        }

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .details(request.getDetails())
                .build();

        return toResponse(reportRepo.save(report));
    }

    public List<ReportResponse> getMyReports(UUID reporterId) {
        return reportRepo.findByReporterId(reporterId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ReportResponse toResponse(Report r) {
        return ReportResponse.builder()
                .id(r.getId())
                .reporterId(r.getReporterId())
                .targetType(r.getTargetType())
                .targetId(r.getTargetId())
                .reason(r.getReason())
                .details(r.getDetails())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
