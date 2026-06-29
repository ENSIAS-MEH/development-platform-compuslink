package com.compuslink.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_cvs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "original_name")
    private String originalName;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private OffsetDateTime uploadedAt;
}
