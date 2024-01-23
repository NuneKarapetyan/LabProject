package com.example.epamProject.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderMedicineId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrdersEntity order;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private MedicineEntity medicine;

}