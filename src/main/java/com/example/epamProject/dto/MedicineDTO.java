package com.example.epamProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineDTO {


    private String name;
    private String dosage;
    private double price;
    private int availableQuantity;
    private LocalDate expirationDate;
    private int ageRestriction;
    private boolean doctorsNote;
    private double rate;
    private String image;
    private String description;
    private boolean isUploaded;

}
