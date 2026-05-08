package com.campuslink.colocation.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Tightly owned by ColocPost — uses @ManyToOne back to parent as specified.
 */
@Entity
@Table(
    name = "coloc_amenities",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_coloc_amenities_post_type",
        columnNames = {"post_id", "amenity_type"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColocAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ColocPost post;

    @Enumerated(EnumType.STRING)
    @Column(name = "amenity_type", nullable = false, columnDefinition = "amenity_type")
    private AmenityType amenityType;
}
