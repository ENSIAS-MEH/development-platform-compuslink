package com.CompusLink.CompusLink.domain.colocation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ColocPostRepository extends JpaRepository<ColocPost, UUID> {
}
