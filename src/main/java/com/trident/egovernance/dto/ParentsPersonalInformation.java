package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.PersonalDetails;

public record ParentsPersonalInformation(
        String motherName,
        String fatherName,
        String localGuardianName,
        String permanentAddress,
        String phone,
        String email
) {
    public ParentsPersonalInformation(PersonalDetails personalDetails) {
        this(
                personalDetails.getMname(),
                personalDetails.getFname(),
                personalDetails.getLgName(),
                personalDetails.getPermanentAddress(),
                personalDetails.getParentContact(),
                personalDetails.getParentEmailId()
        );
    }
}
