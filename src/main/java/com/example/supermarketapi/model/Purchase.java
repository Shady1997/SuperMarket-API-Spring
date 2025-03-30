package com.example.supermarketapi.model;

import com.example.supermarketapi.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Purchase extends BaseEntity {

    @NotNull
    private Double price;

    @NotNull
    private Double changeAmount;

    @NotNull
    private LocalDate timeOfPayment;

    // Add supermarketId to the Purchase entity
    private String supermarketId;

    // Add itemIDs (assuming it's a list of String for simplicity)
    @ElementCollection
    private List<String> itemIDs;

    // Add paymentType as an enum
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    // Add cashAmount (only needed if paymentType is CASH)
    private Double cashAmount;

}
