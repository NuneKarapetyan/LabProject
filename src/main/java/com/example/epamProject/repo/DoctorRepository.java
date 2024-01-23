package com.example.epamProject.repo;

import com.example.epamProject.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Integer> {


    List<DoctorEntity> findAll();

    Page<DoctorEntity> findAll(Pageable pageable);

    List<DoctorEntity> findBySpecialization(String specialization);

    List<DoctorEntity> findByFirstName(String firstName);

    Optional<DoctorEntity> findById(Long id);

    List<DoctorEntity> findByLastName(String lastName);

    List<DoctorEntity> findByFirstNameAndLastName(String firstName, String lastName);
    String getDoctorEmailById(Long id);
    void deleteById(Long id);

    @Query("select d from DoctorEntity d where d.firstName like %?1 or d.lastName like %?1 or d.specialization like %?1")
    List<DoctorEntity> findByFirstNameOrLastNameOrSpecialization(String query);
}
