package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.domains.nsrHandler.services.UserCreationService;
import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.dto.UsernamePurposeOfStaff;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.repositories.permanentDB.StaffRepository;
import com.trident.egovernance.global.services.MiscellaniousServices;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    private final String UPALPHASET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";// Adjust the length as needed
    private final String LOWALPHASET = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMSET = "0123456789";
    private final SecureRandom random = new SecureRandom();
    private final MiscellaniousServices miscellaniousServices;
    private final UserCreationService userCreationService;

    @Autowired
    private final GenericStaffRepoImpl genericStaffRepository;
    @Autowired
    private EmailSenderServiceImpl emailSenderServiceImpl;

    public StaffServiceImpl(StaffRepository staffRepository, MiscellaniousServices miscellaniousServices, UserCreationService userCreationService, GenericStaffRepoImpl genericStaffRepository) {
        this.staffRepository = staffRepository;
        this.miscellaniousServices = miscellaniousServices;
        this.userCreationService = userCreationService;
        this.genericStaffRepository = genericStaffRepository;
    }

    @Transactional
    @Override
    public Staff addStaff(StaffDetailsDto staff) {
        Objects.requireNonNull(staff, "Staff details cannot be null");
        if (staff.staffName() == null || staff.staffName().isEmpty()) {
            throw new IllegalArgumentException("Staff name cannot be empty");
        }

        Staff staffEntity = new Staff(staff);
        staffEntity.setStaffId(staffRepository.getStaffId());
        staffEntity.setUsername(miscellaniousServices.generateStaffUsername(new UsernamePurposeOfStaff(staff.staffDesignation(), staff.role(), staff.staffName())));
        staffEntity.setPassword(generatePassword());
        staffEntity.setEmail(staff.email());

        staffEntity.setSecurityQuestion(Optional.ofNullable(staff.securityQuestion()).orElseThrow(() -> new IllegalArgumentException("Security question cannot be null")));
        staffEntity.setSecurityAnswer(Optional.ofNullable(staff.securityAnswer()).orElse("Not Provided"));

        Staff savedStaff = staffRepository.save(staffEntity);
        logger.info("Staff added successfully - ID: {}, Email: {}", savedStaff.getStaffId(), savedStaff.getEmail());
        return savedStaff;
    }

    private String generatePassword() {
        final int PASSWORD_LENGTH = 12;
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH / 3; i++) {
            password.append(UPALPHASET.charAt(random.nextInt(UPALPHASET.length())));
        }
        for (int i = 0; i < PASSWORD_LENGTH / 3; i++) {
            password.append(NUMSET.charAt(random.nextInt(NUMSET.length())));
        }
        for (int i = 0; i < PASSWORD_LENGTH / 3; i++) {
            password.append(LOWALPHASET.charAt(random.nextInt(LOWALPHASET.length())));
        }
        return password.toString();
    }

    @Transactional
    @Override
    public Boolean updateStaffDetails(StaffDetailsDto updatedStudent, String username) {
        try {
            logger.info(username);
            if (staffRepository.updateStudent(updatedStudent.staffName(), updatedStudent.staffDept(), updatedStudent.staffDesignation(), updatedStudent.staffCategory(), updatedStudent.status(), updatedStudent.role(), updatedStudent.phoneNumber(), updatedStudent.address(), updatedStudent.email(), updatedStudent.securityQuestion(), updatedStudent.securityAnswer(), updatedStudent.collegeName(), username) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException || e instanceof ConstraintViolationException || e instanceof SQLException) {
                logger.error(e.getMessage());
                throw new InvalidInputsException("Invalid data Inputs");
            } else {
                throw new RuntimeException("Unexpected Error Occured");
            }
        }
    }

    @Override
    public List<List<Staff>> getALlStaff() {
        return List.of();
    }

    @Override
    public List<Staff> getStaffByUsername(String username) {
        return staffRepository.getStaffByUsername(username);
    }


    @Override
    public Map<String, List<Object>> getAllStaffDetailsForInput(String entityNames) {
//        if(entityName == null || entityName.trim().isEmpty()) {
//            throw new IllegalArgumentException("Entity name cannot be null or empty");
//        }
//
//        return switch (entityName) {
//            case "StaffDepartmentEntity" -> genericStaffRepository.getFieldValues("STAFFDEPT", "staffDept");
//            case "StaffDesignationEntity" -> genericStaffRepository.getFieldValues("STAFFDESIGNATION", "staffDesignation");
//            case "StaffCategoryEntity" -> genericStaffRepository.getFieldValues("STAFFCATEGORY", "category");
//            case "StaffStatusEntity" -> genericStaffRepository.getFieldValues("STAFFSTATUS", "status");
//            case "StaffRoleEntity" -> genericStaffRepository.getFieldValues("STAFFROLE", "role");
//            default -> Collections.emptyList();
//        };

        if (entityNames == null || entityNames.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty");
        }

        // Split multiple entity names from query param
        String[] entityList = entityNames.split(",");

        // Mapping entity names to actual DB table and column names
        Map<String, String[]> entityMapping = Map.of("StaffDepartmentEntity", new String[]{"STAFFDEPT", "STAFF_DEPT"}, "StaffDesignationEntity", new String[]{"STAFFDESIGNATION", "STAFF_DESIGNATION"},
                "StaffCategoryEntity", new String[]{"STAFFCATEGORY", "CATEGORY"},
                "StaffStatusEntity", new String[]{"STAFFSTATUS", "STATUS"},
                "StaffRoleEntity", new String[]{"STAFFROLE", "ROLE"}
        );

        Map<String, List<Object>> result = new HashMap<>();

        for (String entityName : entityList) {
            if (!entityMapping.containsKey(entityName)) {
                throw new IllegalArgumentException("Invalid entity name: " + entityName);
            }

            // Fetch the correct table and column name
            String tableName = entityMapping.get(entityName)[0];
            String columnName = entityMapping.get(entityName)[1];

            // Query the repository for data
            List<Object> fieldValues = genericStaffRepository.getFieldValues(tableName, columnName);

            // Store the result in the map
            result.put(entityName, fieldValues);
        }
        return result;
    }

    @Override
    public Boolean finalSubmitStaff(Long staffId) {
        try {
            logger.info("Entered inside to finalSubmitStaff for staffId: {}", staffId);

            // 1. Fetch staff record with null check
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> {
                        logger.error("Staff not found with ID: {}", staffId);
                        return new RecordNotFoundException("Staff record not found for ID: " + staffId);
                    });

            logger.info("Fetched staff data from Database for employeeId: {}", staffId);
            logger.debug("Staff details: {}", staff);  // Add debug logging for staff object

            // 2. Generate password
            String password = generatePassword();
            logger.info("Generated temporary password");

            // 3. Create Microsoft account
            String microsoftEmail;
            try {
                microsoftEmail = userCreationService.createStaffUser(
                        staff.getStaffName(),
                        staff.getStaffDesignation(),
                        staff.getUsername(),
                        staff.getStaffDept(),
                        password,
                        staff.getEmail(),
                        staff.getStaffId(),
                        staff.getCollegeName()
                );
                logger.info("Microsoft account created successfully. Response: {}", microsoftEmail);
            } catch (Exception e) {
                logger.error("Failed to create Microsoft account for staff ID: {}", staffId, e);
                throw new ServiceException("Microsoft account creation failed", e);
            }

            // 4. Send email
            try {
                emailSenderServiceImpl.sendTridentCredentialsEmailToStaff(microsoftEmail, password);
                logger.info("Credentials email sent successfully to: {}", microsoftEmail);
            } catch (MessagingException | IOException e) {
                logger.error("Failed to send email to staff ID: {}", staffId, e);
                throw new ServiceException("Email sending failed", e);
            }

            return true;

        } catch (Exception e) {
            logger.error("Error in finalSubmitStaff for staffId: {}", staffId, e);
            throw e; // Or return false if you prefer not to throw
        }
    }

}
