package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.entities.redisEntities.NSR;

public interface MapperService {
    NSRDto convertToNSRDto(NSR nsr);
    NSR convertToNSR(NSRDto nsrDto);

    PersonalDetails convertToPersonalDetails(NSR nsr);
    Student convertToStudent(NSR nsr);
    StudentAdmissionDetails convertToStudentAdmissionDetails(NSR nsr);
    StudentCareer convertToStudentCareer(NSR nsr);
    Transport convertToTransport(NSR nsr);
    Hostel convertToHostel(NSR nsr);
}
