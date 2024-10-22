package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.*;

public record StudentOfficeDTO(
        String regdNo,
        String studentName,
        Courses course,
        String branchCode,
        String phNo,
        String email,
        StudentType studentType,
        Integer currentYear
) {}



