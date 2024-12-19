package com.trident.egovernance.global.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.domains.student.services.StudentDashBoardsServiceImpl;
import com.trident.egovernance.dto.FeeCollectionOnlyDTO;
import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.dto.MoneyDTO;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.StringRecordTemp;
import com.trident.egovernance.global.helpers.SubjectInfo;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.MrDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServices;
import com.trident.egovernance.global.services.SubjectDataFetcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StudentRepository studentRepository;
    private final SubjectDataFetcherService subjectDataFetcherService;
    private final MiscellaniousServices miscellaniousServices;
    private final MasterTableServicesImpl masterTableServicesImpl;
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final FeeCollectionRepository feeCollectionRepository;
    private final StudentDashBoardsServiceImpl studentDashBoardsServiceImpl;

    public TestController(StudentRepository studentRepository, SubjectDataFetcherService subjectDataFetcherService, MiscellaniousServices miscellaniousServices, MasterTableServicesImpl masterTableServicesImpl, FeeCollectionRepository feeCollectionRepository, StudentDashBoardsServiceImpl studentDashBoardsServiceImpl) {
        this.studentRepository = studentRepository;
        this.subjectDataFetcherService = subjectDataFetcherService;
        this.miscellaniousServices = miscellaniousServices;
        this.masterTableServicesImpl = masterTableServicesImpl;

        this.feeCollectionRepository = feeCollectionRepository;
        this.studentDashBoardsServiceImpl = studentDashBoardsServiceImpl;
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
    @PostMapping("/test-FeeCollection/{sessionId}")
    public ResponseEntity<List<FeeCollectionOnlyDTO>> testUpdate(@PathVariable("sessionId") String sessionId){
        return ResponseEntity.ok(feeCollectionRepository.findAllBySessionId(sessionId).stream()
                .map(feeCollection -> new FeeCollectionOnlyDTO(feeCollection))
                .toList());
    }

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
}

