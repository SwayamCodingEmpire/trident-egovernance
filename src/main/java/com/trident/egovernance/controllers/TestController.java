package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.entities.permanentDB.Student;
import com.trident.egovernance.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.services.MasterTableServicesImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StudentRepository studentRepository;
    private final MasterTableServicesImpl masterTableServicesImpl;

    public TestController(StudentRepository studentRepository, MasterTableServicesImpl masterTableServicesImpl) {
        this.studentRepository = studentRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
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
}

