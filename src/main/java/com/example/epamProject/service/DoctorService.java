package com.example.epamProject.service;

import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.DoctorDTO;
import com.example.epamProject.entity.DoctorEntity;
import com.example.epamProject.repo.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final Parser parser;
    private final ModelMapper modelMapper;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, Parser parser,ModelMapper modelMapper) {
        this.doctorRepository = doctorRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;
    }

    public void save(MultipartFile file) {
        try {
            System.out.printf(">>>>>>>>>>>>>Starting the CSV import for Doctors %s%n", new Date());
            List<DoctorEntity> doctors = parser.csvToDoctorEntity(file.getInputStream());
            System.out.println(doctors);
            doctorRepository.saveAll(doctors);
            System.out.printf(">>>>>>>>>>>>>Ending the CSV import for Doctors %s%n", new Date());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CSV data for Doctors: " + e.getMessage());
        }
    }
    public List<DoctorDTO> getAllDoctors() {
        List<DoctorEntity> doctorEntities = doctorRepository.findAll();
        return doctorEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DoctorDTO getDoctorById(Long doctorId) {
        Optional<DoctorEntity> doctorEntityOptional = doctorRepository.findById(doctorId);
        if (doctorEntityOptional.isPresent()) {
            return convertToDTO(doctorEntityOptional.get());
        } else {
            // Handle not found case
            throw new RuntimeException("Doctor not found with ID: " + doctorId);
        }
    }

    private DoctorDTO convertToDTO(DoctorEntity doctorEntity) {
        return modelMapper.map(doctorEntity, DoctorDTO.class);
    }

    public String getDoctorEmailById(Long doctorId) {
        Optional<DoctorEntity> doctorOptional = doctorRepository.findById(doctorId);

        if (doctorOptional.isPresent()) {
            return doctorOptional.get().getEmail();
        } else {
            throw new RuntimeException("Doctor not found for ID: " + doctorId);
        }
    }

    public List<DoctorDTO> searchDoctors(String query) {
        List<DoctorEntity> doctors = doctorRepository.findByFirstNameOrLastNameOrSpecialization(query);
        return doctors.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
