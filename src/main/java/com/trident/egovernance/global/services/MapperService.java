package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.entities.redisEntities.StudentDocData;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.entities.views.SemesterResult;

import java.util.List;
import java.util.Set;

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
    List<DuesDetailsDto> convertToDuesDetailsDto(List<DuesDetails> duesDetailsList);

    List<DuesDetailsDto> convertToDuesDetailsDtoFromOldDuesDetails(List<OldDueDetails> duesDetailsList);
    List<NSRDto> convertToNSRDto(List<NSR> nsrS);

//    List<CollectionReportDTO> convertFromTuplesToListOfCollectionReportDTO(List<Tuple> tuples);
    Set<NSRDto> convertToNSRDtoSet(Set<NSR> nsrS);
    Student convertToStudentFromStudentOnlyDTO(StudentOnlyDTO studentOnlyDTO);

    StudentCareer covertToStudentCareerFromStudentCareerOnlyDTO(StudentCareerOnlyDTO studentCareerOnlyDTO);
    List<StudentOfficeFromDatabaseDTO> convertToStudentOfficeDatabaseDTO(List<StudentOfficeDTO> studentOfficeDTOS);
    List<StudentOfficeDTO> convertToStudentOfficeDTO(List<StudentOfficeFromDatabaseDTO> studentOfficeFromDatabaseDTOS);

    PersonalDetails convertToPersonalDetailsFromPersonalDetailsOnlyDTO(PersonalDetailsOnlyDTO personalDetailsOnlyDTO);

    StudentAdmissionDetails convertToStudentAdmissionDetailsFromStudentAdmissionDetailsOnlyDTO(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsOnlyDTO);

    Hostel convertToHostelFromHostelOnlyDTO(HostelOnlyDTO hostelOnlyDTO);


    Transport convertToTransportFromTransportOnlyDTO(TransportOnlyDTO transportOnlyDTO);

    StudentDocs convertToStudentDocsFromStudentDocsOnlyDTO(StudentDocsOnlyDTO studentDocsOnlyDTO);
    Set<CollectionSummary> convertToCollectionSummarySet(Set<DailyCollectionSummary> dailyCollectionSummaries);
    Set<MrDetailsDTOMinimal> convertToMrDetailsDTOSet(Set<MrDetails> mrDetailsSet);
    Set<FeesOnly> convertToFeesOnly(List<Fees> fees);
    List<FeeTypes> convertToFeeTypesList(Set<FeeTypesOnly> feesList);
    Set<FeeTypesOnly> convertToFeeTypesOnlySet(List<FeeTypes> feesList);
    List<MrDetailsDto> convertToMrDetailsDtoSet(Set<MrDetails> mrDetailsSet);

    List<Subject_Details> convertToSubjectDetailsList(List<SubjectTempDTO> subjectList);

    List<SubjectResultData> convertToSubjectResultsData(List<SemesterResult> semesterResults);
    List<FeeCollectionOnlyDTO> convertToFeeCollectionOnlyDTOList(List<FeeCollection> feeCollections);
    List<MrDetailsDTOMinimal> convertToMrDetailsDTOList(List<MrDetails> mrDetailsList);
//    Set<String> getListOfOtherFees();
}
