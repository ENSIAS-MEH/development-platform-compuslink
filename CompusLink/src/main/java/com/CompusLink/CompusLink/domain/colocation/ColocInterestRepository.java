package com.CompusLink.CompusLink.domain.colocation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ColocInterestRepository extends JpaRepository<ColocInterest, UUID> {
}
