package com.example.Project.repo;

import com.example.Project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("Select u.email from UserEntity u")
    List<String> getAllUserEmails();

    UserEntity getByEmail(String username);

    Boolean existsByEmail(String email);

    //UserEntity findByUserEmailIgnoreCase(String emailId);
    UserEntity findByEmail(String emailId);
}