package com.example.supermarketapi.controller;

import com.example.supermarketapi.dto.AddItemsToSupermarketResponseDTO;
import com.example.supermarketapi.dto.SupermarketInfoDTO;
import com.example.supermarketapi.exception.InvalidDataException;
import com.example.supermarketapi.model.Supermarket;
import com.example.supermarketapi.service.SupermarketService;
import com.example.supermarketapi.validation.WorkingTimeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/supermarkets")
public class SupermarketController {

    private final SupermarketService supermarketService;

    @Autowired
    public SupermarketController(SupermarketService supermarketService) {
        this.supermarketService = supermarketService;
    }

    // Create supermarket with JSON body
    @PostMapping
    public ResponseEntity<Supermarket> createSupermarket(@RequestBody @Valid Supermarket supermarket) {
        // Validate the data from the request body
        if(supermarket.getAddress().length() > 128){
            throw new InvalidDataException("Address exceeds max length of 128!");
        }

        if(supermarket.getName().length() > 64){
            throw new InvalidDataException("Name exceeds max length of 64!");
        }

        if(!supermarket.getPhoneNumber().matches("08[7-9][0-9]{7}")){
            throw new InvalidDataException("Phone number is invalid!");
        }

        if(!WorkingTimeValidator.isValid(supermarket.getWorkHours())){
            throw new InvalidDataException("Working hours are invalid!");
        }

        Supermarket savedSupermarket = supermarketService.createSupermarket(supermarket);
        return new ResponseEntity<>(savedSupermarket, HttpStatus.CREATED);
    }

    // Add items to a supermarket
    @PostMapping("/addItems")
    public ResponseEntity<AddItemsToSupermarketResponseDTO> addItemToSupermarket(@RequestParam @NotNull String supermarketId,
                                                                                 @RequestParam @NotEmpty List<String> itemsIDs) {
        AddItemsToSupermarketResponseDTO addItemsToSupermarketResponseDTO =
                supermarketService.addItems(supermarketId, itemsIDs);
        return new ResponseEntity<>(addItemsToSupermarketResponseDTO, HttpStatus.OK);
    }

    // Get a supermarket by ID
    @GetMapping("/{supermarketId}")
    public ResponseEntity<SupermarketInfoDTO> getSupermarket(@PathVariable String supermarketId) {
        return new ResponseEntity<>(supermarketService.getSupermarketInfo(supermarketId), HttpStatus.OK);
    }

    // Update all fields of a supermarket
    @PutMapping("/{supermarketId}")
    public ResponseEntity<Supermarket> updateSupermarket(@PathVariable String supermarketId,
                                                         @RequestBody @Valid Supermarket supermarket) {
        // Ensure the supermarket exists before attempting to update
        Supermarket existingSupermarket = supermarketService.getSupermarket(supermarketId);
        if (existingSupermarket == null) {
            throw new InvalidDataException("Supermarket with given ID does not exist.");
        }

        // Validate the data from the request body
        if(supermarket.getAddress().length() > 128){
            throw new InvalidDataException("Address exceeds max length of 128!");
        }

        if(supermarket.getName().length() > 64){
            throw new InvalidDataException("Name exceeds max length of 64!");
        }

        if(!supermarket.getPhoneNumber().matches("08[7-9][0-9]{7}")){
            throw new InvalidDataException("Phone number is invalid!");
        }

        if(!WorkingTimeValidator.isValid(supermarket.getWorkHours())){
            throw new InvalidDataException("Working hours are invalid!");
        }

        // If the supermarket is valid, update and save it
        existingSupermarket.setName(supermarket.getName());
        existingSupermarket.setAddress(supermarket.getAddress());
        existingSupermarket.setPhoneNumber(supermarket.getPhoneNumber());
        existingSupermarket.setWorkHours(supermarket.getWorkHours());

        Supermarket updatedSupermarket = supermarketService.createSupermarket(existingSupermarket);
        return new ResponseEntity<>(updatedSupermarket, HttpStatus.OK);
    }

    // Partial update of a supermarket's fields (name, address, phone number, working hours)
    @PatchMapping("/{supermarketId}")
    public ResponseEntity<Supermarket> partialUpdateSupermarket(@PathVariable String supermarketId,
                                                                @RequestBody Supermarket supermarket) {
        // Ensure the supermarket exists before attempting to update
        Supermarket existingSupermarket = supermarketService.getSupermarket(supermarketId);
        if (existingSupermarket == null) {
            throw new InvalidDataException("Supermarket with given ID does not exist.");
        }

        // Validate fields and update only the provided ones
        if (supermarket.getName() != null) {
            if(supermarket.getName().length() > 64){
                throw new InvalidDataException("Name exceeds max length of 64!");
            }
            existingSupermarket.setName(supermarket.getName());
        }

        if (supermarket.getAddress() != null) {
            if(supermarket.getAddress().length() > 128){
                throw new InvalidDataException("Address exceeds max length of 128!");
            }
            existingSupermarket.setAddress(supermarket.getAddress());
        }

        if (supermarket.getPhoneNumber() != null) {
            if(!supermarket.getPhoneNumber().matches("08[7-9][0-9]{7}")){
                throw new InvalidDataException("Phone number is invalid!");
            }
            existingSupermarket.setPhoneNumber(supermarket.getPhoneNumber());
        }

        if (supermarket.getWorkHours() != null) {
            if(!WorkingTimeValidator.isValid(supermarket.getWorkHours())){
                throw new InvalidDataException("Working hours are invalid!");
            }
            existingSupermarket.setWorkHours(supermarket.getWorkHours());
        }

        Supermarket updatedSupermarket = supermarketService.createSupermarket(existingSupermarket);
        return new ResponseEntity<>(updatedSupermarket, HttpStatus.OK);
    }

    // Delete a supermarket by ID
    @DeleteMapping("/{supermarketId}")
    public ResponseEntity<String> deleteSupermarket(@PathVariable String supermarketId) {
        Supermarket existingSupermarket = supermarketService.getSupermarket(supermarketId);
        if (existingSupermarket == null) {
            throw new InvalidDataException("Supermarket with given ID does not exist.");
        }

        supermarketService.deleteSupermarket(supermarketId);
        return new ResponseEntity<>("Supermarket successfully deleted", HttpStatus.NO_CONTENT);
    }

    // Get all supermarkets
    @GetMapping
    public ResponseEntity<List<Supermarket>> getAllSupermarkets() {
        List<Supermarket> supermarkets = supermarketService.getAllSupermarkets();
        return new ResponseEntity<>(supermarkets, HttpStatus.OK);
    }
}
