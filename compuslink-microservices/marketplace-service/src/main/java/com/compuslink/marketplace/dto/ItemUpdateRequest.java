package com.compuslink.marketplace.dto;

import com.compuslink.marketplace.model.ItemCondition;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ItemUpdateRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String city;
    private ItemCondition condition;
    private String category;
    private List<UUID> removeImageIds;
    private List<MultipartFile> newImages;
}
