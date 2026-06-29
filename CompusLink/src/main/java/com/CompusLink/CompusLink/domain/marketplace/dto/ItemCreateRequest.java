package com.CompusLink.CompusLink.domain.marketplace.dto;

import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotBlank
    private String city;

    @NotNull
    private ItemCondition condition;

    @NotBlank
    private String category;

    @NotNull
    @Size(min = 1, message = "At least one image is required")
    private List<MultipartFile> images;
}
