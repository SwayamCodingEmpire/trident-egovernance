package com.trident.egovernance.dto;

public record StudentDetailsDTO(
        String regdNo,
        String phoneNumber,
        String email,
        String address,
        String city,
        Long pincode,
        String guardianName
) {}