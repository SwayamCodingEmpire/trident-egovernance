package com.trident.egovernance.domains.hrHandler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class StaffController {
    @PostMapping("/create")
    public ResponseEntity<String> createStaff(){
        return ResponseEntity.ok("Successfully created staff");
    }
}
