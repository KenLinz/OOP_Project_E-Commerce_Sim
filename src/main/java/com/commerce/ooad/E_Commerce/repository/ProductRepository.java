package com.commerce.ooad.E_Commerce.repository;

import com.commerce.ooad.E_Commerce.model.ProductSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductSQL, Long> {

}