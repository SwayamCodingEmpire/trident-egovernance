package com.trident.egovernance.domains.officeHandler.web;

import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.CourseStudentCountDTO;
import com.trident.egovernance.dto.StudentCountDto;
import com.trident.egovernance.dto.StudentOfficeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/office")
public class OfficeController {
    private final OfficeServicesImpl officeServices;

    public OfficeController(OfficeServicesImpl officeServices) {
        this.officeServices = officeServices;
    }
    @GetMapping("/continuing-students")
    public ResponseEntity<List<StudentOfficeDTO>> getAllContinuingStudents(){
        return ResponseEntity.ok(officeServices.getAllContinuingStudents());
    }

    @GetMapping("/alumni-students")
    public ResponseEntity<List<StudentOfficeDTO>> getAllAlumniStudents(){
        return ResponseEntity.ok(officeServices.getAllAlumniStudents());
    }

    @GetMapping("/count-All-Students")
    public ResponseEntity<StudentCountDto> getNoOfStudents(){
        return ResponseEntity.ok(new StudentCountDto(officeServices.countAllContinuingStudents(),officeServices.countAllAlumningStudents()));
    }

    @GetMapping("/grouped-student-count/{status}")
    public ResponseEntity<List<CourseStudentCountDTO>> getGroupedStudentsData(@PathVariable("status") String status){
        return ResponseEntity.ok(officeServices.getGroupedStudentsCount(status));
    }
}
