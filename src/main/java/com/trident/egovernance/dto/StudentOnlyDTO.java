package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.*;

public record StudentOnlyDTO(
        String regdNo,
        String studentName,
        Gender gender,
        String dob,
        Courses course,
        String branchCode,
        String admissionYear,
        Integer degreeYop,
        String phNo,
        String email,
        StudentType studentType,
        BooleanString hostelier,
        BooleanString transportAvailed,
        StudentStatus status,
        String batchId,
        Integer currentYear,
        Long aadhaarNo,
        BooleanString indortrng,
        BooleanString plpoolm,
        CfPaymentMode cfPayMode,
        Religion religion,
        String section
) implements StudentUpdateDTO {
    public StudentOnlyDTO(Student student) {
        this(
                student.getRegdNo(),
                student.getStudentName(),
                student.getGender(),
                student.getDob(),
                student.getCourse(),
                student.getBranchCode(),
                student.getAdmissionYear(),
                student.getDegreeYop(),
                student.getPhNo(),
                student.getEmail(),
                student.getStudentType(),
                student.getHostelier(),
                student.getTransportAvailed(),
                student.getStatus(),
                student.getBatchId(),
                student.getCurrentYear(),
                student.getAadhaarNo(),
                student.getIndortrng(),
                student.getPlpoolm(),
                student.getCfPayMode(),
                student.getReligion(),
                student.getSection().getSection() == null ? null : student.getSection().getSection()
        );
    }
}
