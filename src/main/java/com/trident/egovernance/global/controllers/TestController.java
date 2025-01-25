package com.trident.egovernance.global.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.domains.student.services.StudentDashBoardsServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.views.Attendance;
import com.trident.egovernance.global.entities.views.RollSheet;
import com.trident.egovernance.global.entities.views.Student_Test;
import com.trident.egovernance.global.helpers.BranchId;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StringRecordTemp;
import com.trident.egovernance.global.helpers.SubjectInfo;
import com.trident.egovernance.global.repositories.permanentDB.*;
import com.trident.egovernance.global.repositories.views.RollSheetRepository;
import com.trident.egovernance.global.repositories.views.StudentTestRepository;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServices;
import com.trident.egovernance.global.services.SessionUpdateService;
import com.trident.egovernance.global.services.SubjectDataFetcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StudentRepository studentRepository;
    private final SectionsRepository sectionsRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final RollSheetRepository rollSheetRepository;
    private final SubjectDataFetcherService subjectDataFetcherService;
    private final MiscellaniousServices miscellaniousServices;
    private final MasterTableServicesImpl masterTableServicesImpl;
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final StudentDashBoardsServiceImpl studentDashBoardsServiceImpl;
    private final StudentAdmissionDetailsRepository studentAdmissionDetailsRepository;
    private final StudentCareerRepository studentCareerRepository;
    private final HostelRepository hostelRepository;
    private final TransportRepository transportRepository;
    private final BranchRepository branchRepository;
    private final SessionUpdateService sessionUpdateService;
    private final StudentTestRepository studentTestRepository;

    public TestController(StudentRepository studentRepository, SectionsRepository sectionsRepository, PersonalDetailsRepository personalDetailsRepository, RollSheetRepository rollSheetRepository, SubjectDataFetcherService subjectDataFetcherService, MiscellaniousServices miscellaniousServices, MasterTableServicesImpl masterTableServicesImpl, StudentDashBoardsServiceImpl studentDashBoardsServiceImpl, StudentAdmissionDetailsRepository studentAdmissionDetailsRepository, StudentCareerRepository studentCareerRepository, HostelRepository hostelRepository, TransportRepository transportRepository, BranchRepository branchRepository, SessionUpdateService sessionUpdateService, StudentTestRepository studentTestRepository) {
        this.studentRepository = studentRepository;
        this.sectionsRepository = sectionsRepository;
        this.personalDetailsRepository = personalDetailsRepository;
        this.rollSheetRepository = rollSheetRepository;
        this.subjectDataFetcherService = subjectDataFetcherService;
        this.miscellaniousServices = miscellaniousServices;
        this.masterTableServicesImpl = masterTableServicesImpl;
        this.studentDashBoardsServiceImpl = studentDashBoardsServiceImpl;
        this.studentAdmissionDetailsRepository = studentAdmissionDetailsRepository;
        this.studentCareerRepository = studentCareerRepository;
        this.hostelRepository = hostelRepository;
        this.transportRepository = transportRepository;
        this.branchRepository = branchRepository;
        this.sessionUpdateService = sessionUpdateService;
        this.studentTestRepository = studentTestRepository;
    }

    @GetMapping("/hello")
    public String hello(){
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(customUserDetails.getNsr().toString());
        System.out.println(customUserDetails);
        return "Hello World";
    }

    @PostMapping("/student")
    public Student studentTest(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @PostMapping("/feeTypesMrHead")
    public List<FeeTypesMrHead> getFeeTypesMrHead(@RequestBody List<String> descriptions){
        return masterTableServicesImpl.getFeeTypesMrHeadByDescriptions(descriptions);
    }
//    @PostMapping("/test-FeeCollection/{session}")
//    public ResponseEntity<List<FeeCollectionOnlyDTO>> testUpdate(@PathVariable("session") String session){
//        return ResponseEntity.ok(feeCollectionRepository.findAllBySessionId(session).stream()
//                .map(feeCollection -> new FeeCollectionOnlyDTO(feeCollection))
//                .toList());
//    }

    @PostMapping("/money-to-words")
    public ResponseEntity<String> convertMoneyToWords(@RequestBody MoneyDTO moneyDTO){
        return ResponseEntity.ok(miscellaniousServices.getMoneyIntoWords(moneyDTO.amount()));
    }

    @GetMapping("/test-query/{regdNo}")
    public void testQuery(@PathVariable String regdNo){
        studentDashBoardsServiceImpl.testQueryForStudentDashboard(regdNo);
    }

    @PostMapping("/get-subject-details")
    public ResponseEntity<List<SubjectInfo>> getSubjectDetails(@RequestBody StringRecordTemp table){
        return ResponseEntity.ok(subjectDataFetcherService.parseSubjectData(table.htmlTable()));
    }

    @GetMapping("/student/{regdNo}")
    public void getStudent(@PathVariable String regdNo){
        Student student = studentRepository.findById(regdNo).orElseThrow(()->new RuntimeException("Student not found"));
        logger.info(student.toString());
    }

    @GetMapping("/students/{regdNo}")
    public void getStudents(@PathVariable String regdNo){

        logger.info("Regdno is {}",studentRepository.findRegdNo(regdNo));
    }

    @GetMapping("/personal-details/{regdNo}")
    public void getPersonalDetails(@PathVariable String regdNo){
        PersonalDetails personalDetails = personalDetailsRepository.findById("2101289364").orElseThrow(()->new RuntimeException("Student not found"));
        logger.info(personalDetails.toString());
    }

    @GetMapping("/sections/{id}")
    public void getSections(@PathVariable Long id){
        Sections sections = sectionsRepository.findById(id).orElseThrow(()->new RuntimeException("Student not found"));
        logger.info(sections.toString());
    }

    @GetMapping("/rollsheet/{regdNo}")
    public void getRollSheet(@PathVariable String regdNo){
        RollSheet rollSheet = rollSheetRepository.findById(regdNo).orElseThrow(()->new RuntimeException("Student not found"));
        logger.info(rollSheet.toString());
    }

//    @GetMapping("/all/{regdNo}")
//    public void getAll(@PathVariable String regdNo){
//        StudentBasicDTO studentBasicDTO = studentRepository.findBasicStudentData(regdNo);
//        logger.info(studentBasicDTO.toString());
//        StudentWithEnumsDTO studentWithEnumsDTO = studentRepository.findStudentWithEnums(regdNo);
//        logger.info(studentWithEnumsDTO.toString());
//        StudentAdmissionDetails studentAdmissionDetails = studentAdmissionDetailsRepository.findByRegdNo(regdNo);
//        logger.info(studentAdmissionDetails.toString());
//        PersonalDetails personalDetails = personalDetailsRepository.findByRegdNo(regdNo);
//        logger.info(personalDetails.toString());
//        StudentCareerOnlyDTO studentCareer = studentCareerRepository.findByRegdNo(regdNo);
//        logger.info(studentCareer.toString());
//        Sections sections = sectionsRepository.findById(1L).orElseThrow(()->new RuntimeException("Student not found"));
//        logger.info(sections.toString());
//        Map<String, Object> ans =  sectionsRepository.findSectionRawById(1L);
//        logger.info(ans.toString());
//        Branch branch = branchRepository.findById(new BranchId("CST","B.TECH.")).orElseThrow(()-> new RuntimeException("Branch not found"));
//        logger.info(branch.toString());
//        Hostel hostel = hostelRepository.findById(regdNo).orElseThrow(()->new RuntimeException("Hostel not found"));
//        logger.info(hostel.toString());
//        Transport transport = transportRepository.findById(regdNo).orElseThrow(()->new RuntimeException("Transport not found"));
//        logger.info(transport.toString());
//        Student st = studentRepository.findStudentWithDetails(regdNo);
//        logger.info(st.toString());
//        Student st1 = studentRepository.findStudentWithRollAndSection(regdNo);
//        logger.info(st1.toString());
//        Student s = studentRepository.findById(regdNo).orElseThrow(()->new RuntimeException("Student not found"));
//        logger.info(s.toString());
//    }
//
//    @GetMapping("/diagnose/{regdNo}")
//    public void geTTest(@PathVariable String regdNo){
//        StudentDetailsDTO studentDetailsDTO = studentRepository.findStudentDetailsDiagnostic(regdNo);
//        logger.info(studentDetailsDTO.toString());
//    }

    @GetMapping("/update-sessions")
    public ResponseEntity<List<Sessions>> updateSessions(){
        return ResponseEntity.ok(sessionUpdateService.updateTestTable1());
    }

    @GetMapping("/get-student-test/{course}")
    public ResponseEntity<List<Student_Test>> getStudentTest(@PathVariable Courses course){
        List<Student_Test> st1 = studentTestRepository.findAllByCourse(course);
        List<Student_Test> output = new ArrayList<>();
        for(Student_Test st : st1){
            Student_Test st2 = new Student_Test(st);
            output.add(st2);
        }
        return ResponseEntity.ok(output);
    }
}

