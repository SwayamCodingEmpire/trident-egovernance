package com.trident.egovernance.domains.officeHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;

import java.util.List;
import java.util.Optional;

public interface OfficeServices {
    List<StudentOfficeDTO> getAllContinuingStudents();

    List<StudentOfficeDTO> getAllAlumniStudents();

    Long countAllContinuingStudents();

    Long countAllAlumningStudents();

    List<CourseStudentCountDTO> getGroupedStudentsCount(String status);

    StudentIndividualRecordFetchDTO getStudentByRegdNo(String regdNo);

    Boolean updateStudentTableOnly(StudentOnlyDTO updatedStudent, String regdNo);

    Boolean updateStudentCareerTable(StudentCareerOnlyDTO studentCareerOnlyDTO, String regdNo);

    Boolean updatePersonalDetailsTable(PersonalDetailsOnlyDTO dto, String regdNo);

    Boolean updateStudentAdmissionDetailsTable(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsDTO, String regdNo);

    Boolean updateStudentDocsTable(List<StudentDocsOnlyDTO> updates, String regdNo);

    List<AdmissionData> getAdmissionData(Optional<String> admissionYear);

    List<TotalAdmissionData> getTotalAdmissionData(Optional<Courses> course, Optional<String> branch);

    List<SessionWiseRecords> getSessionWiseRecords(Optional<StudentStatus> status);

    List<StudentBasicDTO>  fetchStudentDataWithRollSheet(Courses course, String branch, Integer currentYear);

    Boolean initializeSection(SectionFetcher sectionFetcher, String mode);
    SectionFetcher getSectionList(String course, Integer sem, String branchCode, String section);
}