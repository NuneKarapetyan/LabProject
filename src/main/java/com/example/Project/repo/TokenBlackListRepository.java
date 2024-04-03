package com.example.Project.repo;

import com.example.Project.entity.TokenBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackListEntity, Long>
{
    Boolean existsByToken(String token);
}
