package com.example.supermarketapi.repository;

import com.example.supermarketapi.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // You can add custom queries here if needed
}
