package com.example.supermarketapi.controller;

import com.example.supermarketapi.dto.PurchaseDTO;
import com.example.supermarketapi.exception.InvalidDataException;
import com.example.supermarketapi.exception.ResourceNotFoundException;
import com.example.supermarketapi.model.Purchase;
import com.example.supermarketapi.model.PurchaseRequest;
import com.example.supermarketapi.model.enums.PaymentType;
import com.example.supermarketapi.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseDTO> buyItemsFromSupermarket(@RequestBody @Valid PurchaseRequest request) {
        PaymentType paymentType;
        try {
            paymentType = PaymentType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Invalid type of payment! Valid values are CARD and CASH");
        }

        if (paymentType == PaymentType.CASH && request.getCashAmount() == null) {
            throw new InvalidDataException("Cash amount cannot be null when paying with cash!");
        }

        Purchase purchase = purchaseService.makePurchase(request.getSupermarketId(), request.getItemIDs(), paymentType, request.getCashAmount());
        PurchaseDTO purchaseDTO = new PurchaseDTO(purchase.getPrice(), purchase.getChangeAmount(), purchase.getTimeOfPayment());
        return new ResponseEntity<>(purchaseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAll() {
        List<PurchaseDTO> response = purchaseService.getAll().stream()
                .map(purchase -> new PurchaseDTO(purchase.getPrice(), purchase.getChangeAmount(), purchase.getTimeOfPayment()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get a specific purchase by ID
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        Optional<Purchase> purchaseOptional = purchaseService.getById(id);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found with id: " + id);
        }
        Purchase purchase = purchaseOptional.get();
        PurchaseDTO purchaseDTO = new PurchaseDTO(purchase.getPrice(), purchase.getChangeAmount(), purchase.getTimeOfPayment());
        return new ResponseEntity<>(purchaseDTO, HttpStatus.OK);
    }

    // Partial update (PATCH) a specific purchase
    @PatchMapping("/{id}")
    public ResponseEntity<PurchaseDTO> partialUpdatePurchase(@PathVariable Long id, @RequestBody PurchaseRequest request) {
        // Fetch the existing purchase by its ID
        Optional<Purchase> purchaseOptional = purchaseService.getById(id);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found with id: " + id);
        }

        Purchase purchase = purchaseOptional.get();

        // Only update fields that are not null in the request
        if (request.getSupermarketId() != null) purchase.setSupermarketId(request.getSupermarketId());
        if (request.getItemIDs() != null) purchase.setItemIDs(request.getItemIDs());
        if (request.getType() != null) {
            try {
                PaymentType paymentType = PaymentType.valueOf(request.getType().toUpperCase());
                purchase.setPaymentType(paymentType);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Invalid type of payment! Valid values are CARD and CASH");
            }
        }
        if (request.getCashAmount() != null) purchase.setCashAmount(request.getCashAmount());

        // Save the updated purchase
        purchaseService.update(purchase);

        // Convert the updated Purchase entity to a PurchaseDTO
        PurchaseDTO purchaseDTO = new PurchaseDTO(
                purchase.getPrice(),
                purchase.getChangeAmount(),
                purchase.getTimeOfPayment()
        );

        // Return the PurchaseDTO in the response
        return new ResponseEntity<>(purchaseDTO, HttpStatus.OK);
    }

    // Full update (PUT) a specific purchase
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDTO> fullUpdatePurchase(@PathVariable Long id, @RequestBody PurchaseRequest request) {
        Optional<Purchase> purchaseOptional = purchaseService.getById(id);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found with id: " + id);
        }

        Purchase purchase = purchaseOptional.get();
        // Replace all fields with the request
        purchase.setSupermarketId(request.getSupermarketId());
        purchase.setItemIDs(request.getItemIDs());
        try {
            PaymentType paymentType = PaymentType.valueOf(request.getType().toUpperCase());
            purchase.setPaymentType(paymentType);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Invalid type of payment! Valid values are CARD and CASH");
        }
        purchase.setCashAmount(request.getCashAmount());

        purchaseService.update(purchase);  // Assuming update saves the changes to the database
        PurchaseDTO purchaseDTO = new PurchaseDTO(purchase.getPrice(), purchase.getChangeAmount(), purchase.getTimeOfPayment());
        return new ResponseEntity<>(purchaseDTO, HttpStatus.OK);
    }

    // Delete a specific purchase
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        Optional<Purchase> purchaseOptional = purchaseService.getById(id);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found with id: " + id);
        }

        // Call the service to delete the purchase by ID
        purchaseService.delete(id);

        // Return a response with no content (HTTP 204)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
