package com.trident.egovernance.domains.student.services;

import com.trident.egovernance.dto.AttendanceSummaryDTO;
import com.trident.egovernance.dto.SemesterResultData;
import com.trident.egovernance.dto.StudentProfileDTO;
import com.trident.egovernance.dto.UserJobInformationDto;

import java.util.List;

public interface StudentDashBoardsService {
    StudentProfileDTO getStudentProfile(UserJobInformationDto userJobInformationDto);
    SemesterResultData getSemesterResults();
    List<AttendanceSummaryDTO> getAttendanceSummary();
}
