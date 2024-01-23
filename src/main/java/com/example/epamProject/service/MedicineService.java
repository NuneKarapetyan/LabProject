package com.example.epamProject.service;

import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.MedicineDTO;
import com.example.epamProject.entity.MedicineEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.MedicineRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final Parser parser;
    private final ModelMapper modelMapper;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository, Parser parser,ModelMapper modelMapper) {
        this.medicineRepository = medicineRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;
    }

    public void save(MultipartFile file) {
        try {
            System.out.printf(">>>>>>>>>>>>>Starting the CSV import for Medicines %s%n", new Date());
            List<MedicineEntity> medicines = parser.csvToMedicineEntity(file.getInputStream());
            medicineRepository.saveAll(medicines);
            System.out.printf(">>>>>>>>>>>>>Ending the CSV import for Medicines %s%n", new Date());
        } catch (IOException e) {
            throw new CSVImportException("Failed to store CSV data for medicines: " + e.getMessage());
        }
    }

    public List<MedicineDTO> getAllMedicines() {
        List<MedicineEntity> medicineEntities = medicineRepository.findAll();
        return medicineEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MedicineDTO convertToDTO(MedicineEntity medicineEntity) {
        return modelMapper.map(medicineEntity, MedicineDTO.class);
    }
}
