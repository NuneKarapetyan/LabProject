package com.example.epamProject.service;

import com.example.epamProject.controller.MedicineNameDto;
import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.MedicineDTO;
import com.example.epamProject.entity.MedicineEntity;
import com.example.epamProject.entity.UserEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.MedicineRepository;
import com.example.epamProject.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository,
                           Parser parser, ModelMapper modelMapper, UserRepository userRepository) {
        this.medicineRepository = medicineRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;

        this.userRepository = userRepository;
    }

    public void save(MultipartFile file) {
        try {
            logger.info(">>>>>>>>>>>>>Starting the CSV import for Medicines %s%n" + new Date());
            List<MedicineEntity> medicines = parser.csvToMedicineEntity(file.getInputStream());
            medicineRepository.saveAll(medicines);
            logger.info(">>>>>>>>>>>>>Ending the CSV import for Medicines %s%n" + new Date());
        } catch (IOException e) {
            logger.error("Failed to store CSV data for medicines" + e.getMessage());
            throw new CSVImportException("Failed to store CSV data for medicines: " + e.getMessage());
        }
    }

  /*  public ResponseEntity<?> getAllMedicines(String username) {
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List < MedicineEntity > medicineEntities = medicineRepository.findAll();
      List<MedicineDTO>  medicineDTO= medicineEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
      return ResponseEntity.status(HttpStatus.OK).body(medicineDTO);
    }*/
    public ResponseEntity<?> getAllMedicines(Pageable pageable, String username) {
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Page<MedicineEntity> medicinePage = medicineRepository.findAll(pageable);
        Page<MedicineDTO> medicineDTOPage = medicinePage.map(this::convertToDTO);

        return ResponseEntity.status(HttpStatus.OK).body(medicineDTOPage);
    }

    private MedicineDTO convertToDTO(MedicineEntity medicineEntity) {
        return modelMapper.map(medicineEntity, MedicineDTO.class);
    }

    /*public ResponseEntity<?> getMedicinesByLetter(String letter,String username) {
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Call the repository method to fetch medicines by the starting letter
        List<MedicineEntity> medicines = medicineRepository.findByNameStartingWith(letter);
        List<String> medicineNames = medicines.stream()
                .map(MedicineEntity::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(medicineNames);
    }*/
    public ResponseEntity<?> getMedicinesByLetter(Pageable pageable, String letter, String username) {
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Page<MedicineEntity> medicinePage = medicineRepository.findByNameStartingWith(letter,pageable);
        List<MedicineEntity> medicines = medicineRepository.findByNameStartingWith(letter);
        List<MedicineDTO> medicineDTOs = medicinePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        List<String> medicineNames = medicineDTOs.stream()
                .map(MedicineDTO::getName)
                .collect(Collectors.toList());
        MedicineNameDto medicineNameDto = new MedicineNameDto(medicineNames,medicines.size());

        return ResponseEntity.ok().body(medicineNameDto);
    }

    public ResponseEntity<?> getMedicineDetailsByName(String medicineName,String username) {

            UserEntity user = userRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            MedicineEntity medicineEntity = medicineRepository.findByName(medicineName);
            if (medicineEntity != null) {
                System.out.println(medicineEntity);
                return ResponseEntity.ok().body(convertToDTO(medicineEntity));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found");
            }
    }

  /*  public ResponseEntity<?> searchMedicines(String query,String username) {
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<MedicineEntity> matchingEntities = medicineRepository.findByNameContainingIgnoreCase(query);
        List<MedicineDTO> medicineDTOS = matchingEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok().body(medicineDTOS);
    }*/
    public ResponseEntity<?> searchMedicines(String query, Pageable pageable, String username) {
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<MedicineEntity> medicineEntities = medicineRepository.findByNameContainingIgnoreCase(query);
        Page<MedicineEntity> matchingEntitiesPage = medicineRepository.findByNameContainingIgnoreCase(query, pageable);
        Page<MedicineDTO> medicineDTOSPage = matchingEntitiesPage.map(this::convertToDTO);

        List<String> medicineNames = medicineDTOSPage.stream()
                .map(MedicineDTO::getName)
                .collect(Collectors.toList());

        if(medicineNames.isEmpty()){
            return ResponseEntity.badRequest().body("no medicine has found");
        }
        MedicineNameDto medicineNameDto = new MedicineNameDto(medicineNames,medicineEntities.size());
        return ResponseEntity.ok().body(medicineNameDto);
    }

}
