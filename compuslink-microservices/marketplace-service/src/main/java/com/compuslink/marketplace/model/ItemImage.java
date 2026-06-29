package com.compuslink.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_images")
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, length = 500)
    private String url;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder.Default
    @Column(name = "is_cover", nullable = false)
    private Boolean isCover = false;
}
