package com.example.epamProject.repo;

import com.example.epamProject.entity.BasketItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<BasketItemEntity, Long> {

    BasketItemEntity findByUserEmailAndMedicineName(String username, String medicine);

    BasketItemEntity findByMedicineName(String name);

    List<BasketItemEntity> findByUserEmail(String email);

    void deleteAllByUserEmail(String email);
}