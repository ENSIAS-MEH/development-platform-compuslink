package com.compuslink.colocation.repository;

import com.compuslink.colocation.model.ColocPost;
import com.compuslink.colocation.model.HousingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ColocPostRepository extends JpaRepository<ColocPost, UUID> {
    List<ColocPost> findByPosterId(UUID posterId);

    @Query("SELECT c FROM ColocPost c WHERE c.status = 'OPEN' " +
            "AND (:currentUserId IS NULL OR c.posterId != :currentUserId) " +
            "AND (:city IS NULL OR c.city = :city) " +
            "AND (:type IS NULL OR c.housingType = :type) " +
            "AND (:furnished IS NULL OR c.furnished = :furnished) " +
            "AND (:spotsNeeded IS NULL OR c.spotsNeeded = :spotsNeeded) " +
            "AND (:rentMax IS NULL OR c.rentPerPerson <= :rentMax)")
    Page<ColocPost> findWithFilters(@Param("currentUserId") UUID currentUserId, @Param("city") String city,
            @Param("type") HousingType type, @Param("furnished") Boolean furnished,
            @Param("spotsNeeded") Integer spotsNeeded, @Param("rentMax") BigDecimal rentMax, Pageable pageable);
}
