package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.PersonalDetails;

public record PersonalDetailsOnlyDTO(
        String regdNo,
        String fname,
        String mname,
        String lgName,
        String permanentAddress,
        String permanentCity,
        String permanentState,
        Long permanentPincode,
        String parentContact,
        String parentEmailId,
        String presentAddress
) {
    public PersonalDetailsOnlyDTO(PersonalDetails personalDetails){
        this(
                personalDetails.getRegdNo(),
                personalDetails.getFname(),
                personalDetails.getMname(),
                personalDetails.getLgName(),
                personalDetails.getPermanentAddress(),
                personalDetails.getPermanentCity(),
                personalDetails.getPermanentState(),
                personalDetails.getPermanentPincode(),
                personalDetails.getParentContact(),
                personalDetails.getParentEmailId(),
                personalDetails.getPresentAddress()
        );
    }
}
