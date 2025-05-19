package com.trident.egovernance.domains.hrHandler.controller;

import com.trident.egovernance.domains.hrHandler.services.StaffServiceImpl;
import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.repositories.permanentDB.StaffRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository staffRepository;
    private final StaffServiceImpl staffServiceImpl;
    private final EmailSenderServiceImpl emailSenderServiceImpl;
    private final Logger logger = LoggerFactory.getLogger(StaffController.class);

    public StaffController(StaffRepository staffRepository, StaffServiceImpl staffServiceImpl, EmailSenderServiceImpl emailSenderServiceImpl) {
        this.staffRepository = staffRepository;
        this.staffServiceImpl = staffServiceImpl;
        this.emailSenderServiceImpl = emailSenderServiceImpl;
    }

    @Operation(summary = "Create new staff", description = "Return a response String to indicate that the staff is added successfully or not")
    @PostMapping("/create")
    public ResponseEntity<String> createStaff(@RequestBody StaffDetailsDto staffDetailsDto) {
        try {
            logger.info("Creating new staff: {}", staffDetailsDto);
            staffServiceImpl.addStaff(staffDetailsDto);
            staffServiceImpl.finalSubmitStaff(staffDetailsDto.staffId());
            logger.info("Email sent successfully to the user {}", staffDetailsDto.staffName());
            return ResponseEntity.ok("Staff added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding staff.");
        }
    }

    @Operation(summary = "Update the existing staff details in the table", description = "Replace the previous data with the new data input to the table using username to find the staff")
    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateStaffDetails(
            @PathVariable String username,
            @RequestBody StaffDetailsDto updatedStaff) {
        boolean isUpdated = staffServiceImpl.updateStaffDetails(updatedStaff, username);

        if (isUpdated) {
            return ResponseEntity.ok("Staff details updated successfully for username: " + username);
        } else {
            return ResponseEntity.badRequest().body("Failed to update staff details for username: " + username);
        }
    }

    @Operation(summary = "Get a full list of the total staffs present in the table", description = "Return a list of staff entity from the table using repository queries")
    @GetMapping("/view")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffRepository.getAllStaffs());
    }

    @Operation(summary = "Get the data from the backend to the frontend for dynamic input 'select type option'", description = "Return a hashMap of String and List of Objects values")
    @GetMapping("/dynamic-input")
    public Map<String, List<Object>> getAllStaffDetails(@RequestParam String entityName) {
        return staffServiceImpl.getAllStaffDetailsForInput(entityName);
    }

    @Operation(summary = "Get each staff data specifically by using the username", description = "return a list of staff entity of the particular staff from the table")
    @GetMapping("/get-profile/{username}")
    public List<Staff> getStaffProfile(@PathVariable String username) {
        return staffServiceImpl.getStaffByUsername(username);
    }
}
