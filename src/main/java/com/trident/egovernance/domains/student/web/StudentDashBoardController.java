package com.trident.egovernance.domains.student.web;

import com.trident.egovernance.domains.student.services.StudentDashBoardsServiceImpl;
import com.trident.egovernance.dto.AttendanceSummaryDTO;
import com.trident.egovernance.dto.SemesterResultData;
import com.trident.egovernance.dto.StudentDuesDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student-portal")
public class StudentDashBoardController {
    private final StudentDashBoardsServiceImpl studentDashBoardsService;

    public StudentDashBoardController(StudentDashBoardsServiceImpl studentDashBoardsService) {
        this.studentDashBoardsService = studentDashBoardsService;
    }

//    @GetMapping("/profile")
//    public ResponseEntity<StudentProfileDTO> getStudentProfile() {
//        return ResponseEntity.ok(studentDashBoardsService.getStudentProfile());
//    }

    @GetMapping("/get-dues-details")
    public ResponseEntity<StudentDuesDetails> getStudentDuesDetails() {
        return ResponseEntity.ok(studentDashBoardsService.getStudentDuesDetails());
    }

    @GetMapping("/get-semester-result")
    public ResponseEntity<SemesterResultData> getStudentProfile() {
        return ResponseEntity.ok(studentDashBoardsService.getSemesterResults());
    }

    @GetMapping("/get-attendance-summary")
    public ResponseEntity<List<AttendanceSummaryDTO>> getAttendanceSummary() {
        return ResponseEntity.ok(studentDashBoardsService.getAttendanceSummary());
    }
}
