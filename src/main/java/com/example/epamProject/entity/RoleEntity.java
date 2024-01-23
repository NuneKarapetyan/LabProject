package com.example.epamProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="role")
public class RoleEntity {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public RoleEntity(String name) {
        this.name = name;
    }

    @Column(name = "Name", unique = true, nullable = false)
    private String name;
}
