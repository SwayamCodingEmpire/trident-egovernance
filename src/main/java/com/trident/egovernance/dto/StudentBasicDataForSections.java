package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.Gender;

public record StudentBasicDataForSections(
        String regdNo,
        String name,
        Gender gender,
        String admYear
) {
    public StudentBasicDataForSections(Student student){
        this(
                student.getRegdNo(),
                student.getStudentName(),
                student.getGender(),
                student.getAdmissionYear()
        );
    }
}
