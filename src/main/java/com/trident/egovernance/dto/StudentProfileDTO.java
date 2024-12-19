package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;

public record StudentProfileDTO (
//        String name,
//        String jobTitle,
//        String dept,
        UserJobInformationDto userJobInformationDto,
        StudentCollegeInformation collegeInformation,
        StudentPersonalInformation studentPersonalInformation,
        ParentsPersonalInformation parentsPersonalInformation
) implements ProfileDTO {
    public StudentProfileDTO(Student student, UserJobInformationDto userJobInformationDto) {
        this(
                userJobInformationDto,
                new StudentCollegeInformation(student.getRollSheet().getCollegeRollNo(), student.getCurrentYear(), student.getSection().getSem(), student.getRollSheet().getLabGroup(), student.getSection().getSection(), student.getCourse()),
                new StudentPersonalInformation(student.getPersonalDetails().getPresentAddress(), student.getPhNo(), student.getEmail(), student.getDob()),
                new ParentsPersonalInformation(student.getPersonalDetails())
        );
    }
}
