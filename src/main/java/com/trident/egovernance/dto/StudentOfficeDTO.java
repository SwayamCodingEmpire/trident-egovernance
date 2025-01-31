package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record StudentOfficeDTO(
        String regdNo,
        String studentName,
        String course,
        String branchCode,
        String phNo,
        String email,
        StudentType studentType,
        Integer currentYear,
        String parentContact
) {
    public StudentOfficeDTO(StudentOfficeFromDatabaseDTO std){
        this(
                std.regdNo(),
                std.studentName(),
                std.course().getDisplayName(),
                std.branchCode(),
                std.phNo(),
                std.email(),
                std.studentType(),
                std.currentYear(),
                std.parentContact()
        );
    }
}
