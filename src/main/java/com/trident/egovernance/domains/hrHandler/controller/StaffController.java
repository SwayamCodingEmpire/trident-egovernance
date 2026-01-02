package com.trident.egovernance.domains.hrHandler.controller;

import com.trident.egovernance.domains.hrHandler.services.StaffServiceImpl;
import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.repositories.permanentDB.StaffRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Create new staff",
            description = "Creates staff record and Microsoft account, sends credentials email")
    @PostMapping("/create")
    public ResponseEntity<String> createStaff(@Valid @RequestBody StaffDetailsDto staffDetailsDto) {
        try {
            logger.info("Creating new staff: {}", staffDetailsDto.staffName());
            Staff createdStaff = staffServiceImpl.addStaff(staffDetailsDto);
            logger.info("Staff record created with ID: {}", createdStaff.getStaffId());

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RecordNotFoundException e) {
            logger.error("Staff record not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating staff: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to create staff: " + e.getMessage());
        }

        return ResponseEntity.ok("Successfully created staff");
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
