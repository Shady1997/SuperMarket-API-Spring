package com.example.supermarketapi.service.impl;

import com.example.supermarketapi.model.Item;
import com.example.supermarketapi.repository.ItemRepository;
import com.example.supermarketapi.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();  // Fetches all items from the repository
    }

    @Override
    public Item getItemById(String itemId) {
        Optional<Item> item = itemRepository.findById(itemId);  // Fetches an item by its ID
        return item.orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        // Throws an exception if the item is not found
    }

    @Override
    public boolean deleteItem(String itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (!item.isPresent()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }
        itemRepository.deleteById(itemId);  // Delete the item by ID
        return true;  // Return true if deletion was successful
    }

    @Override
    public Item updateItem(String itemId, Item item) {
        if (!itemRepository.existsById(itemId)) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }
        item.setId(itemId);  // Make sure to retain the existing ID
        return itemRepository.save(item);  // Save the updated item and return it
    }

    @Override
    public Item partialUpdateItem(String itemId, Item item) {
        Optional<Item> existingItemOptional = itemRepository.findById(itemId);
        if (!existingItemOptional.isPresent()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }
        Item existingItem = existingItemOptional.get();

        // Partially update fields that are not null in the provided item
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getPrice() != null) {
            existingItem.setPrice(item.getPrice());
        }
        if (item.getType() != null) {
            existingItem.setType(item.getType());
        }

        return itemRepository.save(existingItem);  // Save the partially updated item and return it
    }
}
