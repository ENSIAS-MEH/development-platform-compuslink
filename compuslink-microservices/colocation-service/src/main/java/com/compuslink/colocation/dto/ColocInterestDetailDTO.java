package com.compuslink.colocation.dto;

import com.compuslink.colocation.model.InterestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ColocInterestDetailDTO {
    private UUID id;
    private UUID postId;
    private String postTitle;
    private UUID posterId;
    private String posterName;
    private Boolean postBlocked;
    private UUID userId;
    private String userName;
    private String initialMessage;
    private InterestStatus status;
    private OffsetDateTime createdAt;
    private List<ColocMessageDTO> messages;
}
