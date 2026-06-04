package com.compuslink.commonservice.dto;
import com.compuslink.commonservice.model.ReportReason;
import com.compuslink.commonservice.model.TargetType;
import lombok.Data;
import java.util.UUID;
@Data public class ReportRequest { private TargetType targetType; private UUID targetId; private ReportReason reason; private String details; }
