package com.example.epamProject.repo;

import com.example.epamProject.entity.TokenBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackListEntity, Long>
{
    Boolean existsByToken(String token);
}
