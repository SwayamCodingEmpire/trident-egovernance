package com.trident.egovernance.domains.student.services;

import com.trident.egovernance.dto.*;

import java.util.List;
import java.util.Map;

public interface StudentDashBoardsService {
    StudentProfileDTO getStudentProfile(UserJobInformationDto userJobInformationDto);
    SemesterResultData getSemesterResultsSortedByCourse();
    Map<Integer, List<AttendanceSummaryDTO>> getAttendanceSummary();
    StudentSubjectWiseResults getResultsGroupedBySemester();
    SemesterResultData getSemesterResultsSortedByBranch();
    List<StudentCareerHistory> getStudentCareerDTO();
}
