package com.example.epamProject.repo;

import com.example.epamProject.entity.MedicineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository  extends JpaRepository<MedicineEntity, Integer> {



    MedicineEntity findByName (String name);
    Page<MedicineEntity> findByNameStartingWith(String startingLetter, Pageable pageable);
    Page<MedicineEntity> findByNameContainingIgnoreCase(String query, Pageable pageable);
    List<MedicineEntity> findByNameContainingIgnoreCase(String query);
    List<MedicineEntity> findByNameStartingWith(String startingLetter);


}

