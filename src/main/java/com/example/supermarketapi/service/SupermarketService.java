package com.example.supermarketapi.service;

import com.example.supermarketapi.dto.AddItemsToSupermarketResponseDTO;
import com.example.supermarketapi.dto.SupermarketInfoDTO;
import com.example.supermarketapi.model.Supermarket;

import java.util.List;

public interface SupermarketService {

    // Method to create a supermarket
    Supermarket createSupermarket(Supermarket supermarket);

    // Method to add items to a supermarket
    AddItemsToSupermarketResponseDTO addItems(String supermarketId, List<String> itemIDs);

    // Method to get supermarket info by ID
    SupermarketInfoDTO getSupermarketInfo(String id);

    // Method to update a supermarket (full update)
    Supermarket updateSupermarket(String supermarketId, Supermarket supermarket);

    // Method to partially update a supermarket
    Supermarket partialUpdateSupermarket(String supermarketId, Supermarket supermarket);

    // Method to delete a supermarket by ID
    void deleteSupermarket(String supermarketId);

    // Method to get all supermarkets
    List<Supermarket> getAllSupermarkets();

    // New method for getting a single supermarket by ID
    Supermarket getSupermarket(String supermarketId);
}
