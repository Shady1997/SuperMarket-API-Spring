package com.example.supermarketapi.controller;

import com.example.supermarketapi.exception.InvalidDataException;
import com.example.supermarketapi.model.Item;
import com.example.supermarketapi.model.enums.ItemType;
import com.example.supermarketapi.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // POST method to create an item, now accepting a request body
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody @Valid Item item) {

        // Validate the data from the request body
        if (item.getName().length() > 64) {
            throw new InvalidDataException("name exceeds max length of 64!");
        }

        if (item.getPrice() < 0.01 || item.getPrice() > 9999.99) {
            throw new InvalidDataException("price should be between 0.01 and 9999.99!");
        }

        boolean anyMatches = Arrays.stream(ItemType.values()).anyMatch(typeOfItem -> item.getType().name().equals(typeOfItem.name()));

        if (!anyMatches) {
            throw new InvalidDataException("Invalid product type! Valid are FOOD, TECHNOLOGY, HOUSEHOLD, DRINKS");
        }

        Item savedItem = itemService.createItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    // GET method to return all items
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();  // Assume the service method returns all items
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    // GET method to return a specific item by itemId
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable String itemId) {
        Item item = itemService.getItemById(itemId);  // Assume the service method returns an item by ID
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if item is not found
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    // DELETE method to delete an item by its ID
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable String itemId) {
        boolean itemExists = itemService.deleteItem(itemId);  // Service method to delete item
        if (!itemExists) {
            return new ResponseEntity<>("Item not found with ID: " + itemId, HttpStatus.NOT_FOUND);  // Return clear message if item not found
        }
        return new ResponseEntity<>("Item successfully deleted", HttpStatus.NO_CONTENT);
    }

    // PUT method to update all fields of an item
    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable String itemId, @RequestBody Item item) {
        Item updatedItem = itemService.updateItem(itemId, item);  // Service method to update the item
        if (updatedItem == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // If update fails, return 500
        }
        // Validate the fields in the request body
        validateItemFields(item);

        // Update the item using the service layer
        updatedItem = itemService.updateItem(itemId, item);

        // Return response based on update result
        if (updatedItem == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // If update fails, return 500
        }
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);  // Return updated item
    }

    // PATCH method to update selected fields of an item (partial update)
    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> partialUpdateItem(@PathVariable String itemId, @RequestBody Item item) {
        Item partiallyUpdatedItem = itemService.partialUpdateItem(itemId, item);  // Service method for partial update
        if (partiallyUpdatedItem == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // If update fails, return 500
        }
        return new ResponseEntity<>(partiallyUpdatedItem, HttpStatus.OK);  // Return partially updated item
    }

    // Utility method to validate item fields
    private void validateItemFields(Item item) {
        if (item.getName() == null || item.getName().length() > 64) {
            throw new InvalidDataException("Name is required and must be less than 64 characters!");
        }
        if (item.getPrice() == null || item.getPrice() < 0.01 || item.getPrice() > 9999.99) {
            throw new InvalidDataException("Price is required and must be between 0.01 and 9999.99!");
        }

        boolean isValidType = Arrays.stream(ItemType.values()).anyMatch(type -> type.name().equals(item.getType().name()));
        if (!isValidType) {
            throw new InvalidDataException("Invalid product type! Valid types are: FOOD, TECHNOLOGY, HOUSEHOLD, DRINKS.");
        }
    }
}
