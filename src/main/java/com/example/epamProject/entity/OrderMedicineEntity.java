package com.example.epamProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordermedicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMedicineEntity {

    @EmbeddedId
    private OrderMedicineId id;

    @Column(name = "quantity")
    private int quantity;
}