package com.compuslink.commonservice.service;

import com.compuslink.common.exception.*;
import com.compuslink.commonservice.dto.*;
import com.compuslink.commonservice.model.*;
import com.compuslink.commonservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List; import java.util.UUID;

@Service @RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepo;
    private final TargetValidationService validation;

    public ReportResponse report(ReportRequest req, UUID reporterId) {
        validation.validate(req.getTargetType(), req.getTargetId());
        Report r = reportRepo.save(Report.builder().reporterId(reporterId)
                .targetType(req.getTargetType()).targetId(req.getTargetId())
                .reason(req.getReason()).details(req.getDetails()).build());
        return new ReportResponse(r.getId(), r.getTargetType(), r.getTargetId(), r.getReason(), r.getStatus(), r.getCreatedAt());
    }
}
