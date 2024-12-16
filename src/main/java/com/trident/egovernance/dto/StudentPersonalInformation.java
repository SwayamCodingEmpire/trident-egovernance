package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.PersonalDetails;

public record StudentPersonalInformation(
        String presentAddress,
        String phoneNo,
        String email,
        String dob
) {
}
