package com.CompusLink.CompusLink.domain.marketplace.dto;

import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ItemUpdateRequest {

    private String title;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String city;

    private ItemCondition condition;

    private String category;

    private List<MultipartFile> newImages;

    private List<UUID> removeImageIds;
}
