package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.Gender;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public record StudentBasicDTO(
        String regdNo,
        String studentName,
        Gender gender,
        String branchCode,
        String admissionYear,
        Integer currentYear,
        String email
) {

    public StudentBasicDTO(Student student){
        this(
                student.getRegdNo(),
                student.getStudentName(),
                student.getGender(),
                student.getBranchCode(),
                student.getAdmissionYear(),
                student.getCurrentYear(),
                student.getEmail()
        );
    }
}