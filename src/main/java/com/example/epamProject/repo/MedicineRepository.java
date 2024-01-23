package com.example.epamProject.repo;

import com.example.epamProject.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository  extends JpaRepository<MedicineEntity, Integer> {

}

