package com.example.supermarketapi.service.impl;

import com.example.supermarketapi.dto.AddItemsToSupermarketResponseDTO;
import com.example.supermarketapi.dto.SupermarketInfoDTO;
import com.example.supermarketapi.exception.DuplicateSupermarketNameException;
import com.example.supermarketapi.exception.InvalidDataException;
import com.example.supermarketapi.exception.SupermarketNotFoundException;
import com.example.supermarketapi.model.Item;
import com.example.supermarketapi.model.Supermarket;
import com.example.supermarketapi.repository.ItemRepository;
import com.example.supermarketapi.repository.SupermarketRepository;
import com.example.supermarketapi.service.SupermarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SupermarketServiceImpl implements SupermarketService {

    private final SupermarketRepository supermarketRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public SupermarketServiceImpl(SupermarketRepository supermarketRepository, ItemRepository itemRepository) {
        this.supermarketRepository = supermarketRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Supermarket createSupermarket(Supermarket supermarket) {
        if (supermarketRepository.findSupermarketByName(supermarket.getName()).isPresent()) {
            throw new DuplicateSupermarketNameException("a supermarket with this name already exists!");
        }
        return supermarketRepository.save(supermarket);
    }

    @Override
    public AddItemsToSupermarketResponseDTO addItems(String supermarketId, List<String> itemIDs) {
        Supermarket supermarket = supermarketRepository.findById(supermarketId).orElseThrow(
                () -> new SupermarketNotFoundException(supermarketId)
        );
        List<String> addedItemsNames = new ArrayList<>();
        Set<Item> supermarketItems = supermarket.getItems();
        for (String itemId : itemIDs) {
            Optional<Item> itemOptional = itemRepository.findById(itemId);
            if (itemOptional.isPresent()) {
                Item item = itemOptional.get();
                supermarketItems.add(item);
                addedItemsNames.add(item.getName());
            }
        }
        System.out.println(addedItemsNames);
        supermarketRepository.save(supermarket);
        return new AddItemsToSupermarketResponseDTO(supermarket.getId(), addedItemsNames);
    }

    @Override
    public SupermarketInfoDTO getSupermarketInfo(String id) {
        Optional<Supermarket> optional = supermarketRepository.findById(id);
        if (optional.isEmpty()) {
            throw new SupermarketNotFoundException(id);
        }
        Supermarket supermarket = optional.get();
        SupermarketInfoDTO supermarketInfoDTO = new SupermarketInfoDTO();
        supermarketInfoDTO.setAddress(supermarket.getAddress());
        supermarketInfoDTO.setName(supermarket.getName());
        supermarketInfoDTO.setPhoneNumber(supermarket.getPhoneNumber());
        supermarketInfoDTO.setWorkHours(supermarket.getWorkHours());
        supermarketInfoDTO.setItems(new ArrayList<>(supermarket.getItems()));
        return supermarketInfoDTO;
    }

    @Override
    public Supermarket updateSupermarket(String supermarketId, Supermarket supermarket) {
        Supermarket existingSupermarket = supermarketRepository.findById(supermarketId).orElseThrow(
                () -> new SupermarketNotFoundException(supermarketId)
        );

        if (supermarket.getName() == null || supermarket.getAddress() == null || supermarket.getPhoneNumber() == null || supermarket.getWorkHours() == null) {
            throw new InvalidDataException("There is a missing field while updating the supermarket!");
        }

        existingSupermarket.setName(supermarket.getName());
        existingSupermarket.setAddress(supermarket.getAddress());
        existingSupermarket.setPhoneNumber(supermarket.getPhoneNumber());
        existingSupermarket.setWorkHours(supermarket.getWorkHours());

        return supermarketRepository.save(existingSupermarket);
    }

    @Override
    public Supermarket partialUpdateSupermarket(String supermarketId, Supermarket supermarket) {
        Supermarket existingSupermarket = supermarketRepository.findById(supermarketId).orElseThrow(
                () -> new SupermarketNotFoundException(supermarketId)
        );

        if (supermarket.getName() != null) {
            existingSupermarket.setName(supermarket.getName());
        }
        if (supermarket.getAddress() != null) {
            existingSupermarket.setAddress(supermarket.getAddress());
        }
        if (supermarket.getPhoneNumber() != null) {
            existingSupermarket.setPhoneNumber(supermarket.getPhoneNumber());
        }
        if (supermarket.getWorkHours() != null) {
            existingSupermarket.setWorkHours(supermarket.getWorkHours());
        }

        return supermarketRepository.save(existingSupermarket);
    }

    @Override
    public void deleteSupermarket(String supermarketId) {
        Supermarket supermarket = supermarketRepository.findById(supermarketId).orElseThrow(
                () -> new SupermarketNotFoundException(supermarketId)
        );
        supermarketRepository.delete(supermarket);
    }

    @Override
    public List<Supermarket> getAllSupermarkets() {
        return supermarketRepository.findAll();
    }

    // New method to get a supermarket by ID
    @Override
    public Supermarket getSupermarket(String supermarketId) {
        return supermarketRepository.findById(supermarketId).orElseThrow(
                () -> new SupermarketNotFoundException(supermarketId)
        );
    }
}
