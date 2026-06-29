package com.compuslink.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "refresh_token_hash", length = 255)
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private UserRole role;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "university", length = 255)
    private String university;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_pic_url", length = 500)
    private String profilePicUrl;

    @Column(name = "cv_url", length = 500)
    private String cvUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
