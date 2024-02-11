package com.example.epamProject.service;

import com.example.epamProject.entity.BasketItemEntity;
import com.example.epamProject.entity.BasketItemStatus;
import com.example.epamProject.entity.MedicineEntity;
import com.example.epamProject.entity.UserEntity;
import com.example.epamProject.repo.BasketRepository;
import com.example.epamProject.repo.MedicineRepository;
import com.example.epamProject.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;


@Service
public class BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final String UPLOAD_DIRECTORY ="C:\\Users\\User\\Downloads\\epamProject\\epamProject\\src\\main\\resources\\receipts\\";


    public ResponseEntity<String> addMedicineToBasket(String username, String medicineName, boolean hasDoctorReceipt) {
        // Find the user by username

        UserEntity user = userRepository.getByEmail(username);
        if (user == null) {
            // User not found
            logger.warn("user  not found with username : " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(username + " not found");
        }

        // Find the medicine by name
        MedicineEntity medicine = medicineRepository.findByName(medicineName);
        if (medicine == null) {
            // Medicine not found
            logger.warn("medicine does not found:" + medicineName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(medicineName + " not found");
        }

        // Check if the medicine requires a doctor receipt
        if (medicine.isRequiresDoctorReceipt() && !hasDoctorReceipt) {
            // Medicine requires doctor receipt but user didn't provide one
            logger.info(medicineName + "is requiring doctors note but it isn't provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(medicineName + "is requiring doctors note but it isn't provided");
        }

        // Create a new basket item and save it
        BasketItemEntity basketItem = new BasketItemEntity();
        basketItem.setUser(user);
        basketItem.setMedicine(medicine);
        basketItem.setQuantity(1); // Assuming the quantity is 1 for simplicity
        basketItem.setStatus(BasketItemStatus.ADDED);
        basketRepository.save(basketItem);
        logger.info(medicineName + "is added to basket");

        return ResponseEntity.ok("Medicine added to basket successfully.");
    }

    public ResponseEntity<String> uploadDoctorReceipt(String username, String medicineName, MultipartFile doctorReceipt) {
        try {
            // Validate the input parameters
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(medicineName) || doctorReceipt == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameters.");
            }

            // Find the user by username
            UserEntity user = userRepository.getByEmail(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
            }

            // Find the medicine by name
            MedicineEntity medicine = medicineRepository.findByName(medicineName);
            if (medicine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found with name: " + medicineName);
            }
                if(doctorReceipt.isEmpty() || doctorReceipt==null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doctors Receipt is missing");

                }
            // Check if the medicine requires a doctor receipt
            if (!medicine.isRequiresDoctorReceipt()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doctor's receipt is not required for this medicine.");
            }

            // Save the uploaded receipt to the local directory
            String fileName = doctorReceipt.getOriginalFilename();
            String filePath =  UPLOAD_DIRECTORY + fileName;
            logger.info("file path is " + filePath);
            File file = new File(filePath);
            doctorReceipt.transferTo(file);

            // Create a new basket item and set the file path
            BasketItemEntity basketItem = new BasketItemEntity();
            basketItem.setUser(user);
            basketItem.setMedicine(medicine);
            basketItem.setQuantity(1); // Assuming quantity is 1 for simplicity
            basketItem.setStatus(BasketItemStatus.ADDED);
            basketItem.setPath(filePath);

            // Save basket item to database
            basketRepository.save(basketItem);

            return ResponseEntity.ok("Doctor's receipt uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload doctor receipt: " + e.getMessage());
        }
    }

    public String getReceiptFilePath(String medicineName) {
        BasketItemEntity basketItem = basketRepository.findByMedicineName(medicineName);
        if (basketItem == null || basketItem.getPath() == null || basketItem.getPath().isEmpty()) {
            return null;
        }
        return basketItem.getPath();
    }
}


