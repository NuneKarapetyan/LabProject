package com.example.epamProject.repo;

import java.util.List;

import com.example.epamProject.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long>
{
    List<SessionEntity> findSessionEntitiesByEmail(String email);

    SessionEntity getById(int id);
}