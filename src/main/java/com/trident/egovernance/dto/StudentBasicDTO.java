package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.Gender;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public record StudentBasicDTO(
        String regdNo,
        String course,
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
                student.getCourse().getDisplayName(),
                student.getStudentName(),
                student.getGender(),
                student.getBranchCode(),
                student.getAdmissionYear(),
                student.getCurrentYear(),
                student.getEmail()
        );
    }
    public StudentBasicDTO(
            String regdNo,
            Courses course,
            String studentName,
            Gender gender,
            String branchCode,
            String admissionYear,
            Integer currentYear,
            String email
    ) {
        // Directly call the record constructor with the processed values
        this(
                regdNo,
                course.getDisplayName(),  // Assuming 'getDisplayName()' returns a String
                studentName,
                gender,
                branchCode,
                admissionYear,
                currentYear,
                email
        );
    }
}