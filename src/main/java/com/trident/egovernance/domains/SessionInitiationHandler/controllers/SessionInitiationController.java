package com.trident.egovernance.domains.SessionInitiationHandler.controllers;

import com.trident.egovernance.domains.SessionInitiationHandler.services.SessionInitiationServiceImpl;
import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.dto.SessionInitiationData;
import com.trident.egovernance.dto.StudentCourse;
import com.trident.egovernance.dto.StudentOnlyDTO;
import com.trident.egovernance.global.entities.permanentDB.Notpromoted;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.services.MasterTableServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/office/initiate-session")
public class SessionInitiationController {
    private final SessionInitiationServiceImpl sessionInitiationService;
    private final OfficeServicesImpl officeServicesImpl;
    private final MasterTableServices masterTableServices;

    public SessionInitiationController(SessionInitiationServiceImpl sessionInitiationService, OfficeServicesImpl officeServicesImpl, MasterTableServices masterTableServices) {
        this.sessionInitiationService = sessionInitiationService;
        this.officeServicesImpl = officeServicesImpl;
        this.masterTableServices = masterTableServices;
    }

    @GetMapping("/get-student-for-promotion")
    public ResponseEntity<List<StudentCourse>> getStudentForPromotion(@RequestParam("admYear") String admYear, @RequestParam("course")Courses course, @RequestParam("regdyear") Integer regdYear, @RequestParam("studentType") StudentType studentType) {
        return ResponseEntity.ok(sessionInitiationService.getStudentsForPromotion(new SessionInitiationDTO(admYear, course, regdYear, studentType)));
    }

//    @PostMapping("/create-new-session")
//    public ResponseEntity<Sessions> createNewSession(@RequestBody SessionInitiationData sessionInitiationDTO) {
//        return ResponseEntity.ok(sessionInitiationService.createNewSession(sessionInitiationDTO));
//    }

//    @PostMapping("/create-new-session")
//    public boolean createNewSession(@RequestBody SessionInitiationDTO sessionInitiationDTO) {}
    @PostMapping("/initiate")
    public ResponseEntity<Boolean> initiateNewSessions(@RequestBody SessionInitiationData sessionInitiationData){
        return ResponseEntity.ok(sessionInitiationService.initiateNewSession(sessionInitiationData));
    }

    @GetMapping("/get-complete-ongoing-sessions")
    public ResponseEntity<List<Sessions>> getCompleteOngoingSessions() {
        return ResponseEntity.ok(masterTableServices.getOngoingSessionsData());
    }

    @GetMapping("/get-not-promoted")
    public ResponseEntity<List<Notpromoted>> getNotPromotedList(
            @RequestParam(required = false) String regdNo,
            @RequestParam(required = false) Integer currentYear,
            @RequestParam(required = false) String sessionId) {

        List<Notpromoted> notPromotedList = sessionInitiationService.getNotPromoted(
                Optional.ofNullable(regdNo),
                Optional.ofNullable(currentYear),
                Optional.ofNullable(sessionId)
        );

        return ResponseEntity.ok(notPromotedList);
    }

    @PostMapping("/promote-not-promoted-students")
    public ResponseEntity<Boolean> promoteNotPromotedStudents(@RequestBody SessionInitiationData sessionInitiationData){
        return ResponseEntity.ok(sessionInitiationService.backUpAndPromoteStudent(sessionInitiationData));
    }

//    @GetMapping("/student-to-promote")
//    public ResponseEntity<Stu>

}
