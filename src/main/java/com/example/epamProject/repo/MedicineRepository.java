package com.example.epamProject.repo;

import com.example.epamProject.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository  extends JpaRepository<MedicineEntity, Integer> {

   /* @Query("SELECT m.name FROM MedicineEntity m WHERE m.name ")
    List<MedicineDTO> findByStartingLetterIgnoreCase(char startingLetter);*/

    List<MedicineEntity> findByNameStartingWith(String startingLetter);
    MedicineEntity findByName (String name);
    List<MedicineEntity> findByNameContainingIgnoreCase(String query);



}

