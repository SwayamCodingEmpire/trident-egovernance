package com.trident.egovernance.domains.nsrHandler.web;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.domains.nsrHandler.NSRService;
import com.trident.egovernance.global.helpers.RankType;
import com.trident.egovernance.global.services.CourseFetchingServiceImpl;
import com.trident.egovernance.global.services.MapperServiceImpl;
import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/NSR")
class NSRController {
    private final NSRService nsrService;
    private final CourseFetchingServiceImpl courseFetchingService;
    private final Logger logger = LoggerFactory.getLogger(NSRController.class);
    private final MapperServiceImpl mapperService;

    public NSRController(NSRService nsrService, CourseFetchingServiceImpl courseFetchingService, MapperServiceImpl mapperService) {
        this.nsrService = nsrService;
        this.courseFetchingService = courseFetchingService;
        this.mapperService = mapperService;
    }

    @PostMapping("/post")
    public ResponseEntity<NSRDto> postNSRData(@RequestBody NSR nsr,BindingResult rBindingResult){
        logger.info(nsr.toString());
        if(rBindingResult.hasErrors()){
            throw new InvalidInputsException(rBindingResult.getFieldError().getDefaultMessage());
        }
        return ResponseEntity.ok(nsrService.postNSRData(nsr));
    }

    @PostMapping("/bulk-post")
    public ResponseEntity<Boolean> bulkPostNSRData(@RequestBody @Valid List<NSR> nsrs,BindingResult rBindingResult){
        logger.info(nsrs.toString());
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        List<String> errorMessages = new ArrayList<>();
        // Iterate through each NSR and validate it manually
        for (NSR nsr : nsrs) {
            Set<ConstraintViolation<NSR>> violations = validator.validate(nsr);
            // Collect errors if validation fails
            for (ConstraintViolation<NSR> violation : violations) {
                String jeeApplicationNo = nsr.getJeeApplicationNo();
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String errorMessage = String.format("Error in element with jeeApplicationNo '%s': Field '%s' %s",
                        jeeApplicationNo, field, message);
                errorMessages.add(errorMessage);
            }
            if(violations.isEmpty()){
                int duration = courseFetchingService.getCourseDetails(nsr.getCourse()).getDuration();
                nsr.setAdmissionYear(String.valueOf(Year.now()));
                nsr.setDegreeYop(Year.now().getValue() + duration);
                if(nsr.getRankType().equals(RankType.JEE)){
                    nsr.setAieeeRank(nsr.getRank().toString());
                }
                else {
                    nsr.setOjeeRank(nsr.getRank().toString());
                }
                nsr.setRegdNo(nsr.getJeeApplicationNo());
                nsr.setStep(1);
            }
        }

        // If there are errors, throw an exception with the collected messages
        if (!errorMessages.isEmpty()) {
            throw new InvalidInputsException(String.join(", ", errorMessages));
        }
        nsrService.bulkSaveNSRData(nsrs);
        return ResponseEntity.ok(true);
    }
    @PutMapping("/postByStudent")
    public ResponseEntity<NSRDto> postNSRDataByStudentName(@RequestBody NSR nsr){
        try
        {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            NSRDto nsrDto1 = mapperService.convertToNSRDtoList(customUserDetails.getNsr());
            logger.info("After mapperservie success");
            logger.info(nsrDto1.toString());
            logger.info(nsr.toString());
            if (nsrDto1.getJeeApplicationNo().compareTo(nsr.getJeeApplicationNo()) != 0) {
                throw new AccessDeniedException("You are not allowed to post data for this application number");
            }
            logger.info("Before service method call");
            return ResponseEntity.ok(nsrService.postNSRDataByStudent(nsr));
        }
        catch (ClassCastException e){
            throw new AccessDeniedException("Access denied Exception");
        }
    }

    @GetMapping("/getByRollNo/{rollNo}")
    public ResponseEntity<NSRDto> getNSRDataByRollNo(@PathVariable("rollNo") String rollNo){
        return ResponseEntity.ok(nsrService.getNSRDataByRollNo(rollNo));
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

    @GetMapping("/get")
    public ResponseEntity<NSRDto> getTest(){
        try
        {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            NSR nsr = customUserDetails.getNsr();
            logger.info("Request Recieved and sent for processing");
            return ResponseEntity.ok(nsrService.getNSRDataByJeeApplicationNo(nsr.getJeeApplicationNo()));
        }catch (Exception e){
            throw new AccessDeniedException("Access denied Exception");
        }

    }

    @PostMapping("/postByStudent/{jeeApplicationNo}")
    public ResponseEntity<Boolean> finalSubmit(@PathVariable("jeeApplicationNo") String jeeApplicationNo){
//        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        NSRDto nsrDto1 = mapperService.convertToNSRDto(customUserDetails.getNsr());
//        if(nsrDto1.getJeeApplicationNo().compareTo(jeeApplicationNo)!=0){
//            throw new AccessDeniedException("You are not allowed to post data for this application number");
//        }
        return ResponseEntity.ok(nsrService.saveToPermanentDatabase(jeeApplicationNo));
    }

    @GetMapping("/get-by-adm-year/{admYear}")
    public ResponseEntity<Set<NSRDto>> getAllNSRbyAdmissionYear(@PathVariable("admYear") String admissionyear){
        return ResponseEntity.ok(nsrService.getAllNSRDataByAdmissionYear(admissionyear));
    }

//    @GetMapping("/try")
//    public void trying(){
//        String trying = """
//                My Name is SPM.
//                I am a programmer
//                """;
//        System.out.println(trying);
//        return;
//    }
}
