package com.CompusLink.CompusLink.domain.user.repository;

import com.CompusLink.CompusLink.domain.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findByEmail(String email);

    boolean existsByEmail(String email);
}
