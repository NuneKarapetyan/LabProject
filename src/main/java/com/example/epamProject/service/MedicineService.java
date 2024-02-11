package com.example.epamProject.service;

import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.MedicineDTO;
import com.example.epamProject.entity.MedicineEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.MedicineRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);


    @Autowired
    public MedicineService(MedicineRepository medicineRepository, Parser parser,ModelMapper modelMapper) {
        this.medicineRepository = medicineRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;
    }

    public void save(MultipartFile file) {
        try {
            logger.info(">>>>>>>>>>>>>Starting the CSV import for Medicines %s%n"+ new Date());
            List<MedicineEntity> medicines = parser.csvToMedicineEntity(file.getInputStream());
            medicineRepository.saveAll(medicines);
            logger.info(">>>>>>>>>>>>>Ending the CSV import for Medicines %s%n"+ new Date());
        } catch (IOException e) {
            logger.error("Failed to store CSV data for medicines" + e.getMessage());
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
    public List<String> getMedicinesByLetter(String letter) {

        // Call the repository method to fetch medicines by the starting letter
        List<MedicineEntity> medicines = medicineRepository.findByNameStartingWith(letter);
        List<String> medicineNames = medicines.stream()
                .map(MedicineEntity::getName)
                .collect(Collectors.toList());
        return medicineNames;
    }
    public MedicineDTO getMedicineDetailsByName(String medicineName) {
        MedicineEntity medicineEntity = medicineRepository.findByName(medicineName);
        if (medicineEntity != null) {
            return convertToDTO(medicineEntity); // Convert entity to DTO
        } else {
            return null;
        }
    }
    public List<MedicineDTO> searchMedicines(String query) {
        List<MedicineEntity> matchingEntities = medicineRepository.findByNameContainingIgnoreCase(query);
        return matchingEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

}
