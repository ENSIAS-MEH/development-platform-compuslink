package com.compuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "coloc_images")
public class ColocImage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "post_id", nullable = false) private ColocPost post;
    @Column(nullable = false, length = 500) private String url;
    @Builder.Default @Column(name = "sort_order", nullable = false) private Integer sortOrder = 0;
    @Builder.Default @Column(name = "is_cover", nullable = false) private Boolean isCover = false;
}
