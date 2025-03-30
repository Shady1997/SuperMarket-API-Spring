package com.example.supermarketapi.service.impl;

import com.example.supermarketapi.exception.ResourceNotFoundException;
import com.example.supermarketapi.model.Purchase;
import com.example.supermarketapi.model.enums.PaymentType;
import com.example.supermarketapi.repository.PurchaseRepository;
import com.example.supermarketapi.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Purchase makePurchase(String supermarketId, List<String> itemIDs, PaymentType type, Double cashAmount) {
        Purchase purchase = new Purchase();
        purchase.setSupermarketId(supermarketId);  // Set supermarketId
        purchase.setItemIDs(itemIDs);  // Set itemIDs
        purchase.setPaymentType(type);  // Set payment type (CASH or CARD)
        purchase.setCashAmount(cashAmount);  // Set cashAmount

        // For simplicity, assume the price and change calculation logic is implemented
        double price = 100.0; // Example static price
        double change = 0.0;

        if (type == PaymentType.CASH && cashAmount != null) {
            change = cashAmount - price;
        }

        purchase.setPrice(price);
        purchase.setChangeAmount(change);
        purchase.setTimeOfPayment(LocalDate.now());

        // Save the purchase to the database
        return purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public Optional<Purchase> getById(Long id) {
        return purchaseRepository.findById(id);
    }

    @Override
    public Purchase update(Purchase purchase) {
        return purchaseRepository.save(purchase);  // Update the purchase in the database
    }

    @Override
    public void delete(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purchase not found with id: " + id);
        }
        purchaseRepository.deleteById(id);  // Delete the purchase from the database
    }
}
