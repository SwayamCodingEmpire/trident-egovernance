package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.services.MapperServiceImpl;
import com.trident.egovernance.services.NSRServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/NSR")
public class NSRController {
    private final NSRServiceImpl nsrService;
    private final MapperServiceImpl mapperService;

    public NSRController(NSRServiceImpl nsrService, MapperServiceImpl mapperService) {
        this.nsrService = nsrService;
        this.mapperService = mapperService;
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

    @PostMapping("/postByStudent")
    public ResponseEntity<NSRDto> postNSRDataByStudentName(@RequestBody NSRDto nsrDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NSRDto nsrDto1 = mapperService.convertToNSRDto(customUserDetails.getNsr());
        if(nsrDto1.getJeeApplicationNo().compareTo(nsrDto.getJeeApplicationNo())!=0){
            throw new AccessDeniedException("You are not allowed to post data for this application number");
        }
        return ResponseEntity.ok(nsrService.postNSRData(nsrDto));
    }
    @GetMapping("/get-all-nsr")
    public ResponseEntity<List<NSRDto>> getAllNSRData(){
        return ResponseEntity.ok(nsrService.getAllNSRData());
    }
//    @GetMapping("/getByStudentName/{studentName}")
//    public ResponseEntity<Set<NSRDto>> getNSRDataByStudentName(@PathVariable("studentName") String studentName){
//        return ResponseEntity.ok(nsrService.getNSRDataByStudentName(studentName));
//    }

    @PostMapping("/test-post")
    public ResponseEntity<NSRDto> testPost(@Valid @RequestBody NSRDto nsrDto, BindingResult rbindingResult){
        if(rbindingResult.hasErrors()){
            throw new InvalidInputsException(rbindingResult.getFieldError().getDefaultMessage());
        }
        return ResponseEntity.ok(nsrDto);
    }

    @GetMapping("/test-get/{rollNo}")
    public ResponseEntity<NSRDto> getTest(@PathVariable("rollNo") String rollNo){
        return ResponseEntity.ok(nsrService.getNSRDataByRollNo(rollNo));
    }

    @PostMapping("/postByStudent/{jeeApplicationNo}")
    public ResponseEntity<Boolean> finalSubmit(@PathVariable("jeeApplicationNo") String jeeApplicationNo){
//        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        NSRDto nsrDto1 = mapperService.convertToNSRDto(customUserDetails.getNsr());
//        if(nsrDto1.getJeeApplicationNo().compareTo(jeeApplicationNo)!=0){
//            throw new AccessDeniedException("You are not allowed to post data for this application number");
//        }
        return ResponseEntity.ok(nsrService.transferFromTempToPermanentSQLDatabase(jeeApplicationNo));
    }
}
