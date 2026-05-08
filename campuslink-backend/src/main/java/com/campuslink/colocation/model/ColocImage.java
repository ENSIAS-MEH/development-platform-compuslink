package com.campuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Tightly owned by ColocPost — uses @ManyToOne back to parent as specified.
 */
@Entity
@Table(name = "coloc_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColocImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ColocPost post;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_cover", nullable = false)
    @Builder.Default
    private Boolean isCover = false;
}
