package com.trident.egovernance.domains.officeHandler.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.entities.permanentDB.StudentCareer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/office")
public class OfficeController {
    private final OfficeServicesImpl officeServices;
    private final Logger logger = LoggerFactory.getLogger(OfficeController.class);

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

    @GetMapping("/get-student-by-regdNo/{regdNo}")
    public ResponseEntity<StudentIndividualRecordFetchDTO> getStudentByRegdNo(@PathVariable("regdNo") String regdNo){
        logger.info("Method for individual Student called");
        return ResponseEntity.ok(officeServices.getStudentByRegdNo(regdNo));
    }

    @PutMapping("/update-student-data/{table}")
    public ResponseEntity<StudentUpdateDTO> updateStudentData(@PathVariable("table") String table, @RequestBody Object data){
        logger.info("Method for individual Student called");
        return switch (table) {
            case "student" -> ResponseEntity.ok(officeServices.updateStudentTableOnly(new ObjectMapper().convertValue(data, StudentOnlyDTO.class)));
            case "personal-details" -> ResponseEntity.ok(officeServices.updatePersonalDetailsTable(new ObjectMapper().convertValue(data, PersonalDetailsOnlyDTO.class)));
            case "student-admission-details" -> ResponseEntity.ok(officeServices.updateStudentAdmissionDetailsTable(new ObjectMapper().convertValue(data, StudentAdmissionDetailsOnlyDTO.class)));
            case "student-career" -> ResponseEntity.ok(officeServices.updateStudentCareerTable(new ObjectMapper().convertValue(data, StudentCareerOnlyDTO.class)));
            case "hostel" -> ResponseEntity.ok(officeServices.updateHostelTable(new ObjectMapper().convertValue(data, HostelOnlyDTO.class)));
            case "transport" -> ResponseEntity.ok(officeServices.updateTransportTable(new ObjectMapper().convertValue(data, TransportOnlyDTO.class)));
            default -> throw new InvalidInputsException("Invalid table specified: " + table);
        };
    }

    public ResponseEntity<List<StudentDocsOnlyDTO>> updateStudentDocs(@RequestBody List<StudentDocsOnlyDTO> studentDocsOnlyDTOS){
        logger.info("Method for individual Student called");
        return ResponseEntity.ok(officeServices.updateStudentDocsTable(studentDocsOnlyDTOS));
    }
}
