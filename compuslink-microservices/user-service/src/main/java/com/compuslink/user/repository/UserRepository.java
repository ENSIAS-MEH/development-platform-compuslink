package com.compuslink.user.repository;

import com.compuslink.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    Users findByEmail(String email);
    boolean existsByEmail(String email);
}
