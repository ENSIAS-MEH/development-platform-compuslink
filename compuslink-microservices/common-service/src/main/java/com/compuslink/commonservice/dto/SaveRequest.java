package com.compuslink.commonservice.dto;
import com.compuslink.commonservice.model.TargetType;
import lombok.Data;
import java.util.UUID;
@Data public class SaveRequest { private TargetType targetType; private UUID targetId; }
