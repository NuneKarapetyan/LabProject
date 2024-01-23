package com.example.epamProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name",length = 65555)
    private String name;
    @Column(name = "dosage")
    private String dosage;
    @Column(name = "price")
    private double price;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "age_restriction")
    private int ageRestriction;

    @Column(name = "doctors_note")
    private int doctorsNote;

    @Column(name = "rate")
    private double rate;

    @Column(name = "image", length = 65555)
    private String image;
    @Column(name = "description",length = 65555)
    private String description;
}
