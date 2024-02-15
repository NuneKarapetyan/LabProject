package com.example.epamProject.service;

import com.example.epamProject.dto.BasketDto;
import com.example.epamProject.dto.ItemDto;
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
import java.util.ArrayList;
import java.util.List;


@Service
public class BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final String UPLOAD_DIRECTORY = "C:\\Users\\User\\Downloads\\epamProject\\epamProject\\src\\main\\resources\\receipts\\";


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
        System.out.println(hasDoctorReceipt);
        // Check if the medicine requires a doctor receipt
        if (medicine.isRequiresDoctorReceipt() && !hasDoctorReceipt) {
            // Medicine requires doctor receipt but user didn't provide one
            logger.info(medicineName + "is requiring doctors note but it isn't provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(medicineName + "is requiring doctors note but it isn't provided");
        }

        // Create a new basket item and save it
        BasketItemEntity basketItem = basketRepository.findByMedicineName(medicineName);
        System.out.println(medicineName);
        System.out.println(basketItem);

        if(basketItem ==null) {
            BasketItemEntity basketItem1 = new BasketItemEntity();
            basketItem1.setUser(user);
            basketItem1.setMedicine(medicine);
            basketItem1.setQuantity(1); // Assuming the quantity is 1 for simplicity
            if (!medicine.isRequiresDoctorReceipt())
                basketItem1.setStatus(BasketItemStatus.APPROVED);
            else
                basketItem1.setStatus(BasketItemStatus.ADDED);
            basketRepository.save(basketItem1);
        }else{
            basketItem.setUser(user);
            basketItem.setMedicine(medicine);
            basketItem.setQuantity(basketItem.getQuantity() +1);
            if (!medicine.isRequiresDoctorReceipt())
                basketItem.setStatus(BasketItemStatus.APPROVED);
            else
                basketItem.setStatus(BasketItemStatus.ADDED);
            basketRepository.save(basketItem);
        }


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
            if ( doctorReceipt == null || doctorReceipt.isEmpty() ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doctors Receipt is missing");

            }
            // Check if the medicine requires a doctor receipt
            if (!medicine.isRequiresDoctorReceipt()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doctor's receipt is not required for this medicine.");
            }

            // Save the uploaded receipt to the local directory
            String fileName = doctorReceipt.getOriginalFilename();
            String filePath = UPLOAD_DIRECTORY + fileName;
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
            MedicineEntity medicineEntity = medicineRepository.findByName(medicineName);
            medicineEntity.setUploaded(true);

            // Save basket item to database
            basketRepository.save(basketItem);

            return ResponseEntity.ok("Doctor's receipt uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload doctor receipt: " + e.getMessage());
        }
    }

    public ResponseEntity<String> getReceiptFilePath(String medicineName) {
        BasketItemEntity basketItem = basketRepository.findByMedicineName(medicineName);
        if (basketItem == null || basketItem.getPath() == null || basketItem.getPath().isEmpty()) {
          return   ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(basketItem.getPath());
    }

    public BasketDto getUserBasket(String username) {
        // Retrieve basket items for the given username
        List<BasketItemEntity> basketItems = basketRepository.findByUserEmail(username);

        // Initialize variables to store basket details
        List<ItemDto> items = new ArrayList<>();
        double totalCost = 0.0;

        // Iterate through basket items to extract necessary information
        for (BasketItemEntity basketItem : basketItems) {
            if (basketItem.getStatus() == BasketItemStatus.ADDED || basketItem.getStatus() == BasketItemStatus.APPROVED) {
                MedicineEntity medicine = basketItem.getMedicine();
                int quantity = basketItem.getQuantity();
                double price = medicine.getPrice();
                double itemCost = quantity * price;

                // Add item details to the list
                items.add(new ItemDto(medicine.getName(), quantity, itemCost,basketItem.getStatus()));

                // Update total cost
                totalCost += itemCost;
            }
        }

        // Create and return BasketDto object
        return new BasketDto(username, items);

    }
    public ResponseEntity<String> removeMedicineFromBasket(String username, String medicineName) {
        // Find the basket item by username and medicine name
        try {
            BasketItemEntity basketItem = basketRepository.findByUserEmailAndMedicineName(username, medicineName);

            // If basketItem is found, remove it from the repository
            if (basketItem != null) {
                basketRepository.delete(basketItem);
                return ResponseEntity.ok("Medicine removed from basket successfully.");
            }

            // If basketItem is not found, return false
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found in the basket.");
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("bad req");
        }
    }

    public boolean buyMedicines(String username) {
        // Retrieve user's basket
        BasketDto userBasket = getUserBasket(username);

        if (userBasket == null || userBasket.getItems().isEmpty()) {
            return false; // No items in the basket
        }

        // Check if all items are approved for purchase
        for (ItemDto item : userBasket.getItems()) {
            if (!item.getStatus().equals(BasketItemStatus.APPROVED)) {
                return false; // Cannot buy if any item is not approved
            }
            MedicineEntity medicineEntity = medicineRepository.findByName(item.getMedicineName());
            medicineEntity.setAvailableQuantity(medicineEntity.getAvailableQuantity()-item.getQuantity());
        }

        // Process payment (Assuming successful payment)
        // Deduct total cost from user's account balance or initiate payment gateway transaction

        // Update basket status to BOUGHT or remove items from basket

        return updateBasketStatus(username,BasketItemStatus.BOUGHT);
    }
    public boolean updateBasketStatus(String username, BasketItemStatus status) {
        // Retrieve basket items associated with the provided username
        List<BasketItemEntity> basketItems = basketRepository.findByUserEmail(username);

        if (basketItems.isEmpty()) {
            // No basket items found for the user
            return false;
        }

        // Update the status of each basket item
        for (BasketItemEntity basketItem : basketItems) {
            basketItem.setStatus(status);
        }
        // Save the updated basket items back to the database
        basketRepository.saveAll(basketItems);

        return true;
    }

}


