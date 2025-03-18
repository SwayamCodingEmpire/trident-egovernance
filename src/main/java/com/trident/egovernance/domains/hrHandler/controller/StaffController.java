package com.trident.egovernance.domains.hrHandler.controller;

import com.trident.egovernance.domains.hrHandler.services.StaffServiceImpl;
import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.repositories.permanentDB.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository staffRepository;
    private final StaffServiceImpl staffServiceImpl;

    public StaffController(StaffRepository staffRepository, StaffServiceImpl staffServiceImpl) {
        this.staffRepository = staffRepository;
        this.staffServiceImpl = staffServiceImpl;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createStaff(@RequestBody StaffDetailsDto staffDetailsDto){
        try {
            staffServiceImpl.addStaff(staffDetailsDto);
            return ResponseEntity.ok("Staff added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding staff.");
        }
    }

    public ResponseEntity<String> updateStaff(@RequestBody StaffDetailsDto staffDetailsDto){

    }
}
