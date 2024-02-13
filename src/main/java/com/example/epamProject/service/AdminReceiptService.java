package com.example.epamProject.service;

import com.example.epamProject.entity.BasketItemEntity;
import com.example.epamProject.entity.BasketItemStatus;
import com.example.epamProject.repo.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminReceiptService {

    private final BasketRepository basketRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);



    public AdminReceiptService(BasketRepository basketRepository, EmailService emailService) {
        this.basketRepository = basketRepository;
        this.emailService = emailService;
    }


    public boolean validateReceipt(String medicineName, String username, boolean isValid) {

        // Find the basket item by medicine name and username
        BasketItemEntity basketItem = basketRepository.findByUserEmailAndMedicineName(username, medicineName);

        if (basketItem == null) {
            // Basket item not found
            return false;
        }
        if (isValid) {
            // Update the receipt validation status
            logger.info("Admin has approved buying medicine: " + medicineName + "for user: " + username);
            basketItem.setStatus(BasketItemStatus.APPROVED);
            emailService.sendApprovalEmail(username, medicineName);
            logger.info("the approval email has sent to user");
        } else {
            logger.info("Admin has rejected buying medicine: " + medicineName + "for user: " + username);

            basketItem.setStatus(BasketItemStatus.REJECTED);
            emailService.sendRejectionEmail(username, medicineName);
            logger.info("the rejection email has sent to user");
        }

        // Save the updated basket item
        basketRepository.save(basketItem);

        return true; // Validation successful

    }
}
