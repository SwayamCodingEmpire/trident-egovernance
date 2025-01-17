package com.trident.egovernance.domains.officeHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;

import java.util.List;

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

    List<AdmissionData> getAdmissionData(String admissionYear);

    List<TotalAdmissionData> getTotalAdmissionData(Courses course, String branch);

    List<SessionWiseRecords> getSessionWiseRecords(StudentStatus status);
}