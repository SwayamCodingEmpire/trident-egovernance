package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.examDB.SubjectMaster;

public record StudentMasterDTO(
        String subjectCode,
        String subjectName,
        Integer credit,
        String semester,
        String branch
) {
    public StudentMasterDTO(SubjectMaster studentMaster){
        this(
                studentMaster.getSubjectCode(),
                studentMaster.getSubjectName(),
                studentMaster.getCredit(),
                studentMaster.getSemester(),
                studentMaster.getBranch()
        );
    }
}
