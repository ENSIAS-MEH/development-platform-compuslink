package com.compuslink.colocation.dto;

import com.compuslink.colocation.model.ColocStatus;
import com.compuslink.colocation.model.HousingType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ColocPostDTO {
    private UUID id;
    private UUID posterId;
    private String posterName;
    private String title;
    private String description;
    private String city;
    private String address;
    private LocalDate startDate;
    private Integer spotsNeeded;
    private Integer spotsConfirmed;
    private HousingType housingType;
    private BigDecimal rentPerPerson;
    private Boolean furnished;
    private ColocStatus status;
    private String coverUrl;
    private Boolean isBlocked;
    private OffsetDateTime blockedAt;
    private Long totalInterests;
    private Long pendingInterests;
    private List<ColocAmenityDTO> amenities;
    private List<ColocImageDTO> images;
    private OffsetDateTime createdAt;
}
