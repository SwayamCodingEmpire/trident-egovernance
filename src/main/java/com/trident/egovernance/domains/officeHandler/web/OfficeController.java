package com.trident.egovernance.domains.officeHandler.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.entities.permanentDB.StudentCareer;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.repositories.permanentDB.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/office")
public class OfficeController {
    private final OfficeServicesImpl officeServices;
    private final CourseRepository courseRepository;
    private final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    public OfficeController(OfficeServicesImpl officeServices, CourseRepository courseRepository) {
        this.officeServices = officeServices;
        this.courseRepository = courseRepository;
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

    @PutMapping("/update-student-data/{table}/{regdNo}")
    public ResponseEntity<Boolean> updateStudentData(@PathVariable("table") String table, @RequestBody Object data,@PathVariable("regdNo")String regdNo){
        logger.info("Method for individual Student called");
        try{
            return switch (table) {
                case "student" ->
                        ResponseEntity.ok(officeServices.updateStudentTableOnly(new ObjectMapper().convertValue(data, StudentOnlyDTO.class),regdNo));
                case "personal-details" ->
                        ResponseEntity.ok(officeServices.updatePersonalDetailsTable(new ObjectMapper().convertValue(data, PersonalDetailsOnlyDTO.class),regdNo));
                case "student-admission-details" ->
                        ResponseEntity.ok(officeServices.updateStudentAdmissionDetailsTable(new ObjectMapper().convertValue(data, StudentAdmissionDetailsOnlyDTO.class),regdNo));
                case "student-career" ->
                        ResponseEntity.ok(officeServices.updateStudentCareerTable(new ObjectMapper().convertValue(data, StudentCareerOnlyDTO.class),regdNo));
//                case "hostel" ->
//                        ResponseEntity.ok(officeServices.updateHostelTable(new ObjectMapper().convertValue(data, HostelOnlyDTO.class),regdNo));
//                case "transport" ->
//                        ResponseEntity.ok(officeServices.updateTransportTable(new ObjectMapper().convertValue(data, TransportOnlyDTO.class),regdNo));
                default -> throw new InvalidInputsException("Invalid table specified: " + table);
            };
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid Data Inputs for " + table);
        }
    }


    @PutMapping("/update-student-data/student-docs/{regdNo}")
    public ResponseEntity<Boolean> updateStudentDocs(@RequestBody List<StudentDocsOnlyDTO> studentDocsOnlyDTOS, @PathVariable String regdNo){
        logger.info("Method for individual Student called");
        return ResponseEntity.ok(officeServices.updateStudentDocsTable(studentDocsOnlyDTOS,regdNo));
    }

//    @PatchMapping("/update-student-data/student-docs-add/{regdNo}")
//    public ResponseEntity<Boolean> updateStudentDocsTable(@RequestBody List<StudentDocsOnlyDTO> studentDocsOnlyDTOS, @PathVariable String regdNo){
//        logger.info("Method for individual Student called");
//        return ResponseEntity.ok(officeServices.addDocsToStudentDocsTable(studentDocsOnlyDTOS,regdNo));
//    }

    @GetMapping("/constraints-off")
    public void testConstraintsOff(){
        courseRepository.disableAllConstraints();
        logger.info("Constraints-off");
    }

    @GetMapping("/constraints-on")
    public void testConstraintsOn(){
        courseRepository.enableAllConstraints();
        logger.info("Constraints-on");
    }

    @GetMapping("/get-admission-data-year-wise-reports/{admissionYear}")
    public ResponseEntity<List<AdmissionData>> getAdmissionDataYearwiseReports(@PathVariable("admissionYear") String admissionYear){
        return ResponseEntity.ok(officeServices.getAdmissionData(admissionYear));
    }

    @GetMapping("/get-total-admission-data-reports")
    public ResponseEntity<List<TotalAdmissionData>>  getAdmissionDataYearwiseReports(@RequestParam Courses course, @RequestParam String branch){
        return ResponseEntity.ok(officeServices.getTotalAdmissionData(course, branch));
    }

    @GetMapping("/get-session-wise-reports")
    public ResponseEntity<List<SessionWiseRecords>> getAdmissionDataYearwiseReports(@RequestParam StudentStatus status){
        return ResponseEntity.ok(officeServices.getSessionWiseRecords(status));
    }
}
