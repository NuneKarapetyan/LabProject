package com.example.epamProject.service;

import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.UserDto;
import com.example.epamProject.entity.AddressEntity;
import com.example.epamProject.entity.UserEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@EnableTransactionManagement
public class UserService {


    private final UserRepository userRepository;
    private final Parser parser;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(
            UserRepository userRepository,
            Parser parser,
            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;
    }

    public void save(MultipartFile file) {
        try {
            System.out.printf(">>>>>>>>>>>>>Starting the CSV import %s%n", new Date());
            List<UserEntity> users = parser.csvToUserEntity(file.getInputStream());
            // Remove users with email addresses that already exist in the database
            List<String> existingUserEmails = userRepository.getAllUserEmails();
            users.removeIf(user -> existingUserEmails.contains(user.getEmail()));

            // Extract unique addresses from the users and create a map of address IDs to AddressEntity objects
            Map<Integer, AddressEntity> addresses = users.stream()
                    .map(UserEntity::getAddress)
                    .distinct()
                    .collect(Collectors.toMap(AddressEntity::getId, x -> x, (a1, a2) -> a1));

            System.out.println(addresses.size());

            // Replace the address objects in the users with the corresponding address objects from the map
            for (UserEntity user : users) {
                AddressEntity userAddress = user.getAddress();
                user.setAddress(addresses.get(userAddress.getId()));
            }

            userRepository.saveAll(users);
            System.out.printf(">>>>>>>>>>>>>Ending the CSV import %s%n", new Date());
        } catch (IOException e) {
            throw new CSVImportException("Failed to store CSV data: " + e.getMessage());
        }
    }

    public UserDto getByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        return convertToDto(userEntity);
    }

    private UserDto convertToDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

}
