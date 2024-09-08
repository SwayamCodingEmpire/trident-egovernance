package com.trident.egovernance.controllers;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.services.NSRServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/NSR")
public class NSRController {
    private final NSRServiceImpl nsrService;

    public NSRController(NSRServiceImpl nsrService) {
        this.nsrService = nsrService;
    }

    @PostMapping("/post")
    public ResponseEntity<NSRDto> postNSRData(@Valid @RequestBody NSRDto nsrDto,BindingResult rBindingResult){
        if(rBindingResult.hasErrors()){
            throw new InvalidInputsException(rBindingResult.getFieldError().getDefaultMessage());
        }
        return ResponseEntity.ok(nsrService.postNSRData(nsrDto));
    }
    @GetMapping("/getByRollNo/{rollNo}")
    public ResponseEntity<NSRDto> getNSRDataByRollNo(@PathVariable("rollNo") String rollNo){
        return ResponseEntity.ok(nsrService.getNSRDataByRollNo(rollNo));
    }
    @GetMapping("/get-all-nsr")
    public ResponseEntity<List<NSRDto>> getAllNSRData(){
        return ResponseEntity.ok(nsrService.getAllNSRData());
    }
    @GetMapping("/getByStudentName/{studentName}")
    public ResponseEntity<Set<NSRDto>> getNSRDataByStudentName(@PathVariable("studentName") String studentName){
        return ResponseEntity.ok(nsrService.getNSRDataByStudentName(studentName));
    }

    @PostMapping("/test-post")
    public ResponseEntity<NSRDto> testPost(@Valid @RequestBody NSRDto nsrDto, BindingResult rbindingResult){
        if(rbindingResult.hasErrors()){
            throw new InvalidInputsException(rbindingResult.getFieldError().getDefaultMessage());
        }
        return ResponseEntity.ok(nsrDto);
    }
}
