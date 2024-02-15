package com.example.epamProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phoneNumber;
    private String email;
    private String description;
    private double rate;

}
