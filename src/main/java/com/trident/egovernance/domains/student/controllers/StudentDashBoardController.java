package com.trident.egovernance.domains.student.controllers;

import com.trident.egovernance.domains.student.services.StudentDashBoardsServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student-portal")
public class StudentDashBoardController {
    private final StudentDashBoardsServiceImpl studentDashBoardsService;

    public StudentDashBoardController(StudentDashBoardsServiceImpl studentDashBoardsService) {
        this.studentDashBoardsService = studentDashBoardsService;
    }

    @GetMapping("/get-dues-details")
    public ResponseEntity<StudentDuesDetails> getStudentDuesDetails() {
        return ResponseEntity.ok(studentDashBoardsService.getStudentDuesDetails());
    }

    @GetMapping("/get-semester-result/{sorter}")
    public ResponseEntity<SemesterResultData> getStudentProfile(@PathVariable("sorter") String sorter) {
        if(sorter.equals("course")) {
            return ResponseEntity.ok(studentDashBoardsService.getSemesterResultsSortedByCourse());
        }
        else if(sorter.equals("branch")) {
            return ResponseEntity.ok(studentDashBoardsService.getSemesterResultsSortedByBranch());
        }
        else{
            throw new InvalidInputsException("Invalid Path Variable");
        }
    }

    @GetMapping("/get-attendance-summary")
    public ResponseEntity<Map<Integer, List<AttendanceSummaryDTO>>> getAttendanceSummary() {
        return ResponseEntity.ok(studentDashBoardsService.getAttendanceSummary());
    }

    @GetMapping("/get-subject-wise-results")
    public ResponseEntity<StudentSubjectWiseResults> getSubjectWiseResults() {
        return ResponseEntity.ok(studentDashBoardsService.getResultsGroupedBySemester());
    }

    @GetMapping("/get-student-career-history")
    public ResponseEntity<List<StudentCareerHistory>> getStudentCareerHistory() {
        return ResponseEntity.ok(studentDashBoardsService.getStudentCareerDTO());
    }
}
