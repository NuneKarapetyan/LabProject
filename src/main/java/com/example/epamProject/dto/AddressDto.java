package com.example.epamProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressDto {

    private String country;
    private String city;
    private String street;
    private String building;
    private String postalCode;
}
