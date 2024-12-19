package com.trident.egovernance.global.helpers;

public record SubjectInfo(
        Integer serialNumber,
        String subjectCode,
        String subjectName,
        String course,
        String year,
        String semester,
        String pdfLink
) {
}
