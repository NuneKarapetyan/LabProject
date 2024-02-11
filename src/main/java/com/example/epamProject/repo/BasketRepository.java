package com.example.epamProject.repo;

import com.example.epamProject.entity.BasketItemEntity;
import com.example.epamProject.entity.MedicineEntity;
import com.example.epamProject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<BasketItemEntity, Long> {
    BasketItemEntity findByUserAndMedicine(UserEntity user, MedicineEntity medicine);
    BasketItemEntity findByUserEmailAndMedicineName(String username,String medicine);
    BasketItemEntity findByMedicineName(String name);
}