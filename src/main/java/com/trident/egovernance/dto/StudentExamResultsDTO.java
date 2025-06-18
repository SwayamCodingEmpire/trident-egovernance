package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;

public record StudentExamResultsDTO(
        String regdno,
        int semester,
        String subjectCode,
        String grade,
        int credits,
        String resultPublishDate
) {
    public StudentExamResultsDTO(StudentExamResults studentExamResults){
        this(
                studentExamResults.getRegdno(),
                studentExamResults.getSemester(),
                studentExamResults.getSubjectCode(),
                studentExamResults.getGrade(),
                studentExamResults.getCredits(),
                studentExamResults.getResultPublishDate()
        );
    }
}
