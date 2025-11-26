package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserSQL, Long> {
    Optional<UserSQL> findByUsername(String username);
}
