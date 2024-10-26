package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.StudentCareer;

import java.math.BigDecimal;

public record StudentCareerOnlyDTO(
        String regdNo,
        BigDecimal tenthPercentage,
        Long tenthYOP,
        BigDecimal twelvthPercentage,
        Long twelvthYOP,
        BigDecimal diplomaPercentage,
        Long diplomaYOP,
        BigDecimal graduationPercentage,
        Long graduationYOP
) {
    public StudentCareerOnlyDTO(StudentCareer studentCareer) {
        this(
                studentCareer.getRegdNo(),
                studentCareer.getTenthPercentage(),
                studentCareer.getTenthYOP(),
                studentCareer.getTwelvthPercentage(),
                studentCareer.getTwelvthYOP(),
                studentCareer.getDiplomaPercentage(),
                studentCareer.getDiplomaYOP(),
                studentCareer.getGraduationPercentage(),
                studentCareer.getGraduationYOP()
        );
    }
}
