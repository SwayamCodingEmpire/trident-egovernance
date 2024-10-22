package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.DuesDetailsDto;
import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.entities.redisEntities.StudentDocData;

import java.util.List;
import java.util.Set;

public interface MapperService {
    NSRDto convertToNSRDtoList(NSR nsr);
    NSR convertToNSR(NSRDto nsrDto);

    PersonalDetails convertToPersonalDetails(NSR nsr);
    Student convertToStudent(NSR nsr);
    StudentAdmissionDetails convertToStudentAdmissionDetails(NSR nsr);
    StudentCareer convertToStudentCareer(NSR nsr);
    Transport convertToTransport(NSR nsr);
    Hostel convertToHostel(NSR nsr);
    List<StudentDocData> convertToStudentDocData(List<StudentDocs> studentDocs);
    List<StudentDocs> convertToStudentDocs(List<StudentDocData> studentDocData);
    List<DuesDetailsDto> convertToDuesDetailsDto(List<BaseDuesDetails> baseDuesDetailsList);
    List<NSRDto> convertToNSRDtoList(List<NSR> nsrS);

    Set<NSRDto> convertToNSRDtoSet(Set<NSR> nsrS);
}
