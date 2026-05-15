package com.CompusLink.CompusLink.domain.colocation.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CompusLink.CompusLink.domain.colocation.model.ColocPost;
import com.CompusLink.CompusLink.domain.colocation.model.ColocStatus;
import com.CompusLink.CompusLink.domain.colocation.model.HousingType;

public interface ColocPostRepository extends JpaRepository<ColocPost, UUID> {

    List<ColocPost> findByPosterId(UUID posterId);
    List<ColocPost> findByStatus(ColocStatus status);
    List<ColocPost> findByCityIgnoreCase(String city);

    @Query("SELECT p FROM ColocPost p WHERE "
            + "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND "
            + "(:housingType IS NULL OR p.housingType = :housingType) AND "
            + "(:furnished IS NULL OR p.furnished = :furnished) AND "
            + "(:rentMax IS NULL OR p.rentPerPerson <= :rentMax) AND "
            + "(:status IS NULL OR p.status = :status)")

    Page<ColocPost> findWithFilters(
            @Param("city") String city,
            @Param("housingType") HousingType housingType,
            @Param("furnished") Boolean furnished,
            @Param("rentMax") BigDecimal rentMax,
            @Param("status") ColocStatus status,
            Pageable pageable);
}
