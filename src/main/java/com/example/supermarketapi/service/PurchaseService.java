package com.example.supermarketapi.service;

import com.example.supermarketapi.model.Purchase;
import com.example.supermarketapi.model.enums.PaymentType;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {

    Purchase makePurchase(String supermarketId, List<String> itemIDs, PaymentType type, Double cashAmount);

    List<Purchase> getAll();

    Optional<Purchase> getById(Long id);

    Purchase update(Purchase purchase);

    void delete(Long id);  // Add the delete method signature here
}
