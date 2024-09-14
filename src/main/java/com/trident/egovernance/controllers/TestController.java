package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.entities.permanentDB.Student;
import com.trident.egovernance.repositories.permanentDB.StudentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StudentRepository studentRepository;

    public TestController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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
}
