package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.views.SemesterResult;

public record SubjectResultData(
        int semester,
        String subjectCode,
        String grade,
        int credits,
        String subjectName
) {
    public SubjectResultData(SemesterResult semesterResult) {
        this(
                semesterResult.getSemester(),
                semesterResult.getSubjectCode(),
                semesterResult.getGrade(),
                semesterResult.getCredits(),
                (semesterResult.getSubjectDetails() != null && semesterResult.getSubjectDetails().getSubjectName() != null)
                        ? semesterResult.getSubjectDetails().getSubjectName()
                        : "N/A"
        );
    }
}
