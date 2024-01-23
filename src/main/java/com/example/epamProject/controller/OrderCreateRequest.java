package com.example.epamProject.controller;

import com.example.epamProject.dto.OrderMedicineDto;

import java.util.Date;
import java.util.List;

public class OrderCreateRequest {
    private double totalCost;
    private Date orderDate;
    private String doctorsNote;
    private List<OrderMedicineDto> orderMedicines;

    // Getter and Setter methods

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getDoctorsNote() {
        return doctorsNote;
    }

    public void setDoctorsNote(String doctorsNote) {
        this.doctorsNote = doctorsNote;
    }

    public List<OrderMedicineDto> getOrderMedicines() {
        return orderMedicines;
    }

    public void setOrderMedicines(List<OrderMedicineDto> orderMedicines) {
        this.orderMedicines = orderMedicines;
    }
}
