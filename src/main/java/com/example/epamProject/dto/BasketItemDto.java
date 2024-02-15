package com.example.epamProject.dto;

import com.example.epamProject.entity.BasketItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemDto {

    private String medicineName;
    private int quantity;
    private double price;
    private BasketItemStatus status;
}
