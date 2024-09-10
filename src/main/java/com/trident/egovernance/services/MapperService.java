package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.entities.reportingStudent.PersonalDetails;
import com.trident.egovernance.entities.reportingStudent.Student;
import com.trident.egovernance.entities.reportingStudent.StudentAdmissionDetails;
import com.trident.egovernance.entities.reportingStudent.StudentCareer;

public interface MapperService {
    NSRDto convertToNSRDto(NSR nsr);
    NSR convertToNSR(NSRDto nsrDto);

    PersonalDetails convertToPersonalDetails(NSR nsr);
    Student convertToStudent(NSR nsr);
    StudentAdmissionDetails convertToStudentAdmissionDetails(NSR nsr);
    StudentCareer convertToStudentCareer(NSR nsr);
}
