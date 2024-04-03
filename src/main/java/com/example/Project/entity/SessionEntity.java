package com.example.Project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session_registry")
public class SessionEntity
{
    @Id
    @Column(name ="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sessionId", nullable = false)
    private String sessionId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "browserName", nullable = false)
    private String browserName;
}
