package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Roll_Sheet;

public record StudentSectionData(
        String regdNo,
        int labGroup,
        int collegeRollNo
) {

    public StudentSectionData(Roll_Sheet rollSheet){
        this(
                rollSheet.getRegdNo(),
                rollSheet.getLabGroup(),
                rollSheet.getCollegeRollNo()
        );
    }
}
