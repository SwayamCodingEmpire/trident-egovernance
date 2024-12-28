package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record AdmissionData(
        Courses courses,
        String branch,
        StudentType studentType,
        long generalMale,
        long generalFemale,
        long OBCMale,
        long OBCFemale,
        long SCMale,
        long SCFemale,
        long STMale,
        long STFemale,
        long minorityMale,
        long minorityFemale,
        long TFWMale,
        long TFWFemale,
        long NTFWMale,
        long NTFWFemale,
        long totalMale,
        long totalFemale,
        long totalStudents
) {
}
