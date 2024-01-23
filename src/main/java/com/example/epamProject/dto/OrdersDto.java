package com.example.epamProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrdersDto {

    private double totalCost;
    private Date orderDate;
    private String doctorsNote;
    private UserDto user;
    private List<OrderMedicineDto> orderMedicines;
}
