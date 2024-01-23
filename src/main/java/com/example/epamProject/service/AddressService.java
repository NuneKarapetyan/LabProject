package com.example.epamProject.service;

import com.example.epamProject.csv.Parser;
import com.example.epamProject.entity.AddressEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private Parser parser;

    public void save(MultipartFile file) {
        try {
            System.out.printf(">>>>>>>>>>>>>Starting the Address CSV import %s%n", new Date());
            List<AddressEntity> addresses = parser.csvToAddressEntity(file.getInputStream());
            System.out.println(new Date());
            for(AddressEntity a: addresses){
                if(a.getPostalCode()== null || a.getPostalCode().isEmpty()){
                    System.out.println(a.getId());
                }
            }
            addressRepository.saveAll(addresses);
            System.out.printf(">>>>>>>>>>>>>Ending the Address CSV import %s%n", new Date());
        } catch (IOException e) {
            throw new CSVImportException("Failed to store CSV data for addresses: " + e.getMessage());
        }
    }
}

