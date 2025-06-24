package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.examDB.StudentMaster;

public record StudentMasterDTO(
        String subcode,
        String subjectName,
        Integer credit,
        String semester,
        String branch
) {
    public StudentMasterDTO(StudentMaster studentMaster){
        this(
                studentMaster.getSubcode(),
                studentMaster.getSubjectName(),
                studentMaster.getCredit(),
                studentMaster.getSemester(),
                studentMaster.getBranch()
        );
    }
}
