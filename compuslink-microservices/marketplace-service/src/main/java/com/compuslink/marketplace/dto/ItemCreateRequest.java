package com.compuslink.marketplace.dto;

import com.compuslink.marketplace.model.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemCreateRequest {
    @NotBlank private String title;
    private String description;
    @NotNull private BigDecimal price;
    @NotBlank private String city;
    @NotNull private ItemCondition condition;
    @NotBlank private String category;
    private List<MultipartFile> images;
}
