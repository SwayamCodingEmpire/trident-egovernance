package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.*;

public record StudentOfficeFromDatabaseDTO(
        String regdNo,
        String studentName,
        Courses course,
        String branchCode,
        String phNo,
        String email,
        StudentType studentType,
        Integer currentYear,
        String parentContact
) {
    public StudentOfficeFromDatabaseDTO(StudentOfficeDTO std){
        this(
                std.regdNo(),
                std.studentName(),
                Courses.fromDisplayName(std.course()),
                std.branchCode(),
                std.phNo(),
                std.email(),
                std.studentType(),
                std.currentYear(),
                std.parentContact()
        );
    }
}



