package com.trident.egovernance.domains.officeHandler.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.domains.officeHandler.services.OfficeServices;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.repositories.permanentDB.CourseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/office")
@Tag(name = "Office Section - APIS for office", description = "APIs accessible by users with role OFFICE and ADMIN only")
public class OfficeController {
    private final OfficeServices officeServices;
    private final CourseRepository courseRepository;
    private final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    public OfficeController(OfficeServices officeServices, CourseRepository courseRepository) {
        this.officeServices = officeServices;
        this.courseRepository = courseRepository;
    }

    @Operation(summary = "Get student personal data for continuing students", description = "Returns a List of StudentOfficeDTO")
    @GetMapping("/continuing-students")
    public ResponseEntity<List<StudentOfficeDTO>> getAllContinuingStudents(){
        return ResponseEntity.ok(officeServices.getAllContinuingStudents());
    }

    @Operation(summary = "Get student personal data for Alumni students", description = "Returns a List of StudentOfficeDTO")
    @GetMapping("/alumni-students")
    public ResponseEntity<List<StudentOfficeDTO>> getAllAlumniStudents(){
        return ResponseEntity.ok(officeServices.getAllAlumniStudents());
    }
    @Operation(summary = "Count Students", description = "Returns count of Alumni and Continuing Students")
    @GetMapping("/count-All-Students")
    public ResponseEntity<StudentCountDto> getNoOfStudents(){
        return ResponseEntity.ok(new StudentCountDto(officeServices.countAllContinuingStudents(),officeServices.countAllAlumningStudents()));
    }

    @Operation(summary = "Count students with classification by branch, course, and status", description = "Returns a list of CourseStudentCountDTO")
    @GetMapping("/grouped-student-count/{status}")
    public ResponseEntity<List<CourseStudentCountDTO>> getGroupedStudentsData(@PathVariable("status") String status){
        return ResponseEntity.ok(officeServices.getGroupedStudentsCount(status));
    }

    @Operation(summary = "Return student data by regdNo in path variable", description = "Returns a StudentIndividualRecordFetchDTO")
    @GetMapping("/get-student-by-regdNo/{regdNo}")
    public ResponseEntity<StudentIndividualRecordFetchDTO> getStudentByRegdNo(@PathVariable("regdNo") String regdNo){
        logger.info("Method for individual Student called");
        return ResponseEntity.ok(officeServices.getStudentByRegdNo(regdNo));
    }

    @Operation(summary = "Updates student datain the table", description = "Replaces the data in the table with the inputted data")
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


    @Operation(summary = "Update the student docs table")
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

//    @Operation(summary = "Dont touch this")
//    @GetMapping("/constraints-off")
//    public void testConstraintsOff(){
//        courseRepository.disableAllConstraints();
//        logger.info("Constraints-off");
//    }
//
//    @Operation(summary = "Dont touch this")
//    @GetMapping("/constraints-on")
//    public void testConstraintsOn(){
//        courseRepository.enableAllConstraints();
//        logger.info("Constraints-on");
//    }

    @Operation(summary = "Admission Report year wise")
    @GetMapping("/get-admission-data-year-wise-reports/{admissionYear}")
    public ResponseEntity<List<AdmissionData>> getAdmissionDataYearwiseReports(@PathVariable("admissionYear") Optional<String> admissionYear){
        return ResponseEntity.ok(officeServices.getAdmissionData(admissionYear));
    }

    @Operation(summary = "Total Admission Report with Request param course and branch")
    @GetMapping("/get-total-admission-data-reports")
    public ResponseEntity<List<TotalAdmissionData>>  getAdmissionDataYearwiseReports(@RequestParam Optional<Courses> course, @RequestParam Optional<String> branch){
        return ResponseEntity.ok(officeServices.getTotalAdmissionData(course, branch));
    }

    @Operation(summary = "Session wise Report with query params as Status")
    @GetMapping("/get-session-wise-reports")
    public ResponseEntity<List<SessionWiseRecords>> getSessionwiseRecords(@RequestParam Optional<StudentStatus> status){
        return ResponseEntity.ok(officeServices.getSessionWiseRecords(status));
    }

    @GetMapping("/get-student-for-sections")
    public ResponseEntity<List<StudentBasicDTO>> getStudentForSections(@RequestParam("course") Courses course, @RequestParam("branch") String branch, @RequestParam("currentYear") Integer currentYear){
        return ResponseEntity.ok(officeServices.fetchStudentDataWithRollSheet(course, branch, currentYear));
    }

    @GetMapping("/get-section-data")
    public ResponseEntity<SectionFetcher> getSectionData(@RequestParam("course") Courses course, @RequestParam("branchCode") String branchCode, @RequestParam("sem") Integer sem, @RequestParam("section") String section){
        return ResponseEntity.ok(officeServices.getSectionList(course.getDisplayName(), sem, branchCode, section));
    }

    @PostMapping("/sections/{mode}")
    public ResponseEntity<Boolean> createSections(@RequestBody SectionFetcher sectionFetcher, @PathVariable String mode){
        logger.info(sectionFetcher.toString());
        return ResponseEntity.ok(officeServices.initializeSection(sectionFetcher, mode));
    }
}
