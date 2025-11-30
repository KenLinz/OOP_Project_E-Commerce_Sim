package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserSQL, Long> {
    Optional<UserSQL> findByUsername(String username);
    Optional<UserSQL> findByEmail(String email);
}
