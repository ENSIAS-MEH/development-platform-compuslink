package com.CompusLink.CompusLink.domain.common.repository;

import com.CompusLink.CompusLink.domain.common.model.Report;
import com.CompusLink.CompusLink.domain.common.model.ReportStatus;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByReporterId(UUID reporterId);
    List<Report> findByTargetTypeAndTargetId(TargetType targetType, UUID targetId);
    List<Report> findByStatus(ReportStatus status);
    boolean existsByReporterIdAndTargetTypeAndTargetId(UUID reporterId, TargetType targetType, UUID targetId);
}
