package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.StudentAdmissionDetails;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.TFWType;

import java.sql.Date;

public record StudentAdmissionDetailsOnlyDTO(
        String regdNo,
        Date admissionDate,
        BooleanString ojeeCounsellingFeePaid,
        TFWType tfw,
        String admissionType,
        String ojeeRollNo,
        String ojeeRank,
        String aieeeRank,
        String caste,
        Date reportingDate,
        String categoryCode,
        Long categoryRank,
        String jeeApplicationNo,
        String allotmentId
) {
    public StudentAdmissionDetailsOnlyDTO(StudentAdmissionDetails admissionDetails){
        this(
                admissionDetails.getRegdNo(),
                admissionDetails.getAdmissionDate(),
                admissionDetails.getOjeeCounsellingFeePaid(),
                admissionDetails.getTfw(),
                admissionDetails.getAdmissionType(),
                admissionDetails.getOjeeRollNo(),
                admissionDetails.getOjeeRank(),
                admissionDetails.getAieeeRank(),
                admissionDetails.getCaste(),
                admissionDetails.getReportingDate(),
                admissionDetails.getCategoryCode(),
                admissionDetails.getCategoryRank(),
                admissionDetails.getJeeApplicationNo(),
                admissionDetails.getAllotmentId()
        );
    }
}
