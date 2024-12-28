package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record SessionWiseRecords(
        String sessionId,
        String course,
        String branch,
        String studentType,
        int courseYear,
        long noOfMales,
        long noOfFemales,
        long totalStudents

) {
}
