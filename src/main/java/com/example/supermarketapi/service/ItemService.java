package com.example.supermarketapi.service;

import com.example.supermarketapi.model.Item;

import java.util.List;

public interface ItemService {

    // Method to create an item
    public Item createItem(Item item);

    // Method to get all items
    public List<Item> getAllItems();

    // Method to get an item by its ID
    public Item getItemById(String itemId);

    // Method to delete an item by its ID
    public boolean deleteItem(String itemId);

    // Method to update all fields of an item by its ID
    public Item updateItem(String itemId, Item item);

    // Method to partially update an item (update selected fields)
    public Item partialUpdateItem(String itemId, Item item);
}
