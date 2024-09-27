package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.entities.permanentDB.Student;
import com.trident.egovernance.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.services.MasterTableServices;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StudentRepository studentRepository;
    private final MasterTableServices masterTableServices;

    public TestController(StudentRepository studentRepository, MasterTableServices masterTableServices) {
        this.studentRepository = studentRepository;
        this.masterTableServices = masterTableServices;
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
        return masterTableServices.getFeeTypesMrHeadByDescriptions(descriptions);
    }
}

