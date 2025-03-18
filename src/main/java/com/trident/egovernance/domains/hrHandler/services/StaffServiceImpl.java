package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.repositories.permanentDB.StaffRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    private final String UPALPHASET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";// Adjust the length as needed
    private final String LOWALPHASET = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMSET = "0123456789";
    private final SecureRandom random = new SecureRandom();

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Transactional
    @Override
    public void addStaff(StaffDetailsDto staff) {
        Objects.requireNonNull(staff, "Staff details cannot be null");

        Staff staffEntity = new Staff(staff);
        staffEntity.setStaffId(staffRepository.getStaffId());

        String username = generateStaffUsername(staff.staffName(), staff.collegeName(),
                staff.staffDept());
        staffEntity.setUsername(username);
        staffEntity.setPassword(generatePassword());

        staffEntity.setSecurityQuestion(Optional.ofNullable(staff.securityQuestion())
                .orElseThrow(() -> new IllegalArgumentException("Security question cannot be null")));
        staffEntity.setSecurityAnswer(Optional.ofNullable(staff.securityAnswer()).orElse("Not Provided"));

        staffRepository.save(staffEntity);
        logger.info("Staff added successfully: {}", username);
    }

    private String generatePassword() {
        final int PASSWORD_LENGTH = 12;
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH/3; i++) {
            password.append(UPALPHASET.charAt(random.nextInt(UPALPHASET.length())));
        }
        for (int i = 0; i < PASSWORD_LENGTH/3; i++) {
            password.append(NUMSET.charAt(random.nextInt(NUMSET.length())));
        }
        for (int i = 0; i < PASSWORD_LENGTH/3; i++) {
            password.append(LOWALPHASET.charAt(random.nextInt(LOWALPHASET.length())));
        }
        return password.toString();
    }

    private String generateStaffUsername(String staffName, String collegeName, String staffDept) {
        String firstFourLetterOfCollege = collegeName.substring(0,4);
        if (staffName == null || staffName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }

        // Split the name into parts
        String[] nameParts = staffName.trim().toLowerCase().split("\\s+");
        if (nameParts.length == 0) {
            throw new IllegalArgumentException("Full name must contain at least a first name");
        }

        String firstName = nameParts[0]; // Always at least the first name
        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : ""; // Optional last name

        // Combine parts to form UPN
        return lastName.isEmpty()
                ? String.format("%s.%s@%s.onmicrosoft.com", firstName.replace(" ", ""),firstFourLetterOfCollege,
                staffDept)
                : String.format(
                "%s.%s.%s@%s.onmicrosoft.com",
                firstName.replace(" ", ""),
                lastName,
                firstFourLetterOfCollege,
                staffDept);
    }

    @Transactional
    @Override
    public Boolean updateStaffDetails(StaffDetailsDto updatedStudent, String username) {
        try {
            logger.info(username);
            if (staffRepository.updateStudent(
                    updatedStudent.staffName(),
                    updatedStudent.staffDept(),
                    updatedStudent.staffDesignation(),
                    updatedStudent.staffCategory(),
                    updatedStudent.status(),
                    updatedStudent.role(),
                    updatedStudent.phoneNumber(),
                    updatedStudent.address(),
                    updatedStudent.email(),
                    updatedStudent.securityQuestion(),
                    updatedStudent.securityAnswer(),
                    updatedStudent.collegeName(),
                    username
            ) == 1) {
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
    public Staff getStaffByUsername(String username) {
        return null;
    }


    @Override
    public List<String> getAllStaffDetailsForInput(String entityName) {
        if(entityName == null || entityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty");
        }

//        return switch (entityName) {
//            case "StaffDepartmentEntity" -> Arrays.stream(StaffDept.values())
//                    .map(StaffDept::name)
//                    .toList();
//            case "StaffDesignationEntity" -> Arrays.stream(StaffDesignation.values())
//                    .map(StaffDesignation::name)
//                    .toList();
//            case "StaffCategoryEntity" -> Arrays.stream(StaffCategory.values())
//                    .map(StaffCategory::name)
//                    .toList();
//            case "StaffStatusEntity" -> Arrays.stream(StaffStatus.values())
//                    .map(StaffStatus::name)
//                    .toList();
//            case "StaffRoleEntity" -> Arrays.stream(StaffRole.values())
//                    .map(StaffRole::name)
//                    .toList();
//            default -> Collections.emptyList();
//        };

        return null;
    }
}
