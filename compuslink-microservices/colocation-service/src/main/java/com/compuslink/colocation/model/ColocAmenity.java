package com.compuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "coloc_amenities", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "amenity_type"}))
public class ColocAmenity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "post_id", nullable = false) private ColocPost post;
    @Enumerated(EnumType.STRING) @Column(name = "amenity_type", nullable = false) private AmenityType amenityType;
}
