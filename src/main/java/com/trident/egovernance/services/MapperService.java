package com.trident.egovernance.services;

import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.entities.redisEntities.StudentDocData;

import java.util.List;

public interface MapperService {
    NSRDto convertToNSRDto(NSR nsr);
    NSR convertToNSR(NSRDto nsrDto);

    PersonalDetails convertToPersonalDetails(NSR nsr);
    Student convertToStudent(NSR nsr);
    StudentAdmissionDetails convertToStudentAdmissionDetails(NSR nsr);
    StudentCareer convertToStudentCareer(NSR nsr);
    Transport convertToTransport(NSR nsr);
    Hostel convertToHostel(NSR nsr);
    List<StudentDocData> convertToStudentDocData(List<StudentDocs> studentDocs);
    List<StudentDocs> convertToStudentDocs(List<StudentDocData> studentDocData);
}
