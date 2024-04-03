package com.example.Project.repo;

import com.example.Project.entity.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {

    ConfirmationTokenEntity findByConfirmationToken(String confirmationToken);

    ConfirmationTokenEntity findByUserEmail(String email);
}