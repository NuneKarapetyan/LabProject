package com.example.epamProject.dto;

import com.example.epamProject.entity.BasketItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String medicineName;
    private int quantity;
    private double totalCost;
    private BasketItemStatus status;
}
