package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.entities.redisEntities.StudentDocData;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import jakarta.persistence.Tuple;

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
    List<DuesDetailsDto> convertToDuesDetailsDto(List<DuesDetails> duesDetailsList);

    List<DuesDetailsDto> convertToDuesDetailsDtoFromOldDuesDetails(List<OldDueDetails> duesDetailsList);
    List<NSRDto> convertToNSRDtoList(List<NSR> nsrS);

    List<CollectionReportDTO> convertFromTuplesToListOfCollectionReportDTO(List<Tuple> tuples);
    Set<NSRDto> convertToNSRDtoSet(Set<NSR> nsrS);
    Student convertToStudentFromStudentOnlyDTO(StudentOnlyDTO studentOnlyDTO);

    StudentCareer covertToStudentCareerFromStudentCareerOnlyDTO(StudentCareerOnlyDTO studentCareerOnlyDTO);

    PersonalDetails convertToPersonalDetailsFromPersonalDetailsOnlyDTO(PersonalDetailsOnlyDTO personalDetailsOnlyDTO);

    StudentAdmissionDetails convertToStudentAdmissionDetailsFromStudentAdmissionDetailsOnlyDTO(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsOnlyDTO);

    Hostel convertToHostelFromHostelOnlyDTO(HostelOnlyDTO hostelOnlyDTO);


    Transport convertToTransportFromTransportOnlyDTO(TransportOnlyDTO transportOnlyDTO);

    StudentDocs convertToStudentDocsFromStudentDocsOnlyDTO(StudentDocsOnlyDTO studentDocsOnlyDTO);
    Set<CollectionSummary> convertToCollectionSummarySet(Set<DailyCollectionSummary> dailyCollectionSummaries);

//    Set<String> getListOfOtherFees();
}
