package com.example.epamProject.csv;

import com.example.epamProject.entity.*;
import com.example.epamProject.repo.AddressRepository;
import com.example.epamProject.repo.DoctorRepository;
import com.example.epamProject.repo.RoleRepository;
import com.example.epamProject.repo.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Parser {

    /* public List<UserEntity> csvToUserEntity(InputStream is) {
         try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
              CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                      CSVFormat.EXCEL.withDelimiter(',')
                              .withSkipHeaderRecord(true)
                              .withTrim()
                              .withIgnoreEmptyLines(true)
                              .withQuote('"')
                              .withIgnoreHeaderCase()
                              .withHeader(
                                      "first_name", "last_name", "phone_number", "email", "password",
                                      "address_name", "city", "country", "postal_code", "image", "age", "id"))) {

             List<UserEntity> listOfUsers = new ArrayList<>();
             Iterable<CSVRecord> csvRecords = csvParser.getRecords();

             for (CSVRecord csvRecord : csvRecords) {
                 if (Integer.parseInt(csvRecord.get("age")) > 0) {
                     Long addressId = Long.parseLong(csvRecord.get("addressID"));
                     AddressEntity address = addressRepository.findById(addressId)
                             .orElseThrow(() -> new RuntimeException("Address not found for ID: " + addressId));
                     UserEntity user = new UserEntity();
                     user.setFirstName(csvRecord.get("first_name"));
                     user.setLastName(csvRecord.get("last_name"));
                     user.setPhoneNumber(csvRecord.get("phone_number"));
                     user.setEmail(csvRecord.get("email"));
                     user.setPassword(csvRecord.get("password"));
                     user.setAddress(Long.valueOf(csvRecord.get("addressId")));
                     user.setImage(csvRecord.get("image"));
                     user.setAge(Integer.parseInt(csvRecord.get("age")));
                     user.setId(Long.parseLong(csvRecord.get("id")));

                     listOfUsers.add(user);
                 }
             }
             return listOfUsers;
         } catch (IOException e) {
             throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
         }
     }*/
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DoctorRepository doctorRepository;


    public List<AddressEntity> csvToAddressEntity(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.EXCEL.withDelimiter(',')
                             .withSkipHeaderRecord(true)
                             .withTrim()
                             .withIgnoreEmptyLines(true)
                             .withQuote('"')
                             .withIgnoreHeaderCase()
                             .withHeader("id",  "country", "city","street", "building","postal_code"))) {

            List<AddressEntity> listOfAddresses = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                AddressEntity address = new AddressEntity();
                address.setId(Integer.parseInt(csvRecord.get("id")));
                address.setCountry(csvRecord.get("country"));
                address.setCity(csvRecord.get("city"));
                address.setStreet(csvRecord.get("street"));
                address.setBuilding(csvRecord.get("building"));
                address.setPostalCode(csvRecord.get("postal_code"));
                if (address.getPostalCode() == null || address.getPostalCode().isEmpty()) {
                    address.setPostalCode("7023");
                }
                listOfAddresses.add(address);
            }
            return listOfAddresses;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file for addresses: " + e.getMessage());
        }
    }
    public List<DoctorEntity> csvToDoctorEntity(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.EXCEL.withDelimiter(',')
                             .withSkipHeaderRecord(true)
                             .withTrim()
                             .withIgnoreEmptyLines(true)
                             .withQuote('"')
                             .withIgnoreHeaderCase()
                             .withHeader("id", "specialization","description","email","first_name",
                                     "last_name","image","rate","phone_number"))) {

            List<DoctorEntity> listOfDoctors = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                DoctorEntity doctor = new DoctorEntity();
                doctor.setId(Integer.parseInt(csvRecord.get("id")));
                doctor.setSpecialization(csvRecord.get("specialization"));
                doctor.setDescription(csvRecord.get("description"));
                doctor.setEmail(csvRecord.get("email"));
                doctor.setFirstName(csvRecord.get("first_name"));
                doctor.setLastName(csvRecord.get("last_name"));
                doctor.setImage(csvRecord.get("image"));
                doctor.setRate(Double.parseDouble(csvRecord.get("rate")));
                doctor.setPhoneNumber(csvRecord.get("phone_number"));
                listOfDoctors.add(doctor);
            }
            return listOfDoctors;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file for addresses: " + e.getMessage());
        }
    }


    public List<UserEntity> csvToUserEntity(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.EXCEL.withDelimiter(',')
                             .withSkipHeaderRecord(true)
                             .withTrim()
                             .withIgnoreEmptyLines(true)
                             .withQuote('"')
                             .withIgnoreHeaderCase()
                             .withHeader(
                                     "id", "first_name", "last_name", "email", "phone_number","addressID",
                                     "image", "password","role_id","age"))) {

            List<UserEntity> listOfUsers = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                if (Integer.parseInt(csvRecord.get("age")) > 0) {
                    // Fetch AddressEntity from the database based on addressID
                    Long addressId = Long.parseLong(csvRecord.get("addressID"));
                    AddressEntity address = addressRepository.findById(addressId)
                            .orElseThrow(() -> new RuntimeException("Address not found for ID: " + addressId));

                    Integer roleId = Integer.valueOf(csvRecord.get("role_id"));
                    RoleEntity role = roleRepository.findById(Long.valueOf(roleId))
                            .orElseThrow( ()-> new RuntimeException("Role not found for ID: " + roleId));
                    // Create UserEntity
                    UserEntity user = new UserEntity();
                    user.setFirstName(csvRecord.get("first_name"));
                    user.setLastName(csvRecord.get("last_name"));
                    user.setPhoneNumber(csvRecord.get("phone_number"));
                    user.setEmail(csvRecord.get("email"));
                    user.setPassword(csvRecord.get("password"));
                    user.setAddress(address);
                    user.setImage(csvRecord.get("image"));
                    user.setAge(Integer.parseInt(csvRecord.get("age")));
                    user.setId(Integer.parseInt(csvRecord.get("id")));
                    user.setRole(role);

                    listOfUsers.add(user);
                }
            }
            return listOfUsers;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    public List<MedicineEntity> csvToMedicineEntity(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.EXCEL.withDelimiter(',')
                             .withSkipHeaderRecord(true)
                             .withTrim()
                             .withIgnoreEmptyLines(true)
                             .withQuote('"')
                             .withIgnoreHeaderCase()
                             .withHeader("id","age_restriction",  "available_quantity", "doctors_note","dosage",
                                     "expiration_date", "name", "price",  "rate","image","description"))) {

            List<MedicineEntity> listOfMedicines = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            for (CSVRecord csvRecord : csvRecords) {
                MedicineEntity medicine = new MedicineEntity();
                medicine.setId(Integer.parseInt(csvRecord.get("id")));
                medicine.setName(csvRecord.get("name"));
                medicine.setDosage(csvRecord.get("dosage")+"mg");
                medicine.setPrice(Double.parseDouble(csvRecord.get("price")));
                medicine.setAvailableQuantity(Integer.parseInt(csvRecord.get("available_quantity")));
                try {
                    LocalDate expirationDate = LocalDate.parse(csvRecord.get("expiration_date"), dateFormatter);
                    medicine.setExpirationDate(expirationDate);
                } catch (DateTimeParseException e) {
                    medicine.setExpirationDate(LocalDate.now()); // Set a default value or handle it as needed
                }
                medicine.setAgeRestriction(Integer.parseInt(csvRecord.get("age_restriction")));
                medicine.setDoctorsNote(Integer.parseInt(csvRecord.get("doctors_note")));
                medicine.setRate(Double.parseDouble(csvRecord.get("rate")));
                medicine.setImage(csvRecord.get("image"));
                medicine.setDescription(csvRecord.get("description"));

                listOfMedicines.add(medicine);
            }
            return listOfMedicines;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file for medicines: " + e.getMessage());
        }
    }
}
