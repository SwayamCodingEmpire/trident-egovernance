package com.trident.egovernance.domains.SessionInitiationHandler.web;

import com.trident.egovernance.domains.SessionInitiationHandler.services.SessionInitiationServiceImpl;
import com.trident.egovernance.domains.officeHandler.services.OfficeServicesImpl;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.dto.SessionInitiationData;
import com.trident.egovernance.dto.StudentOnlyDTO;
import com.trident.egovernance.global.entities.permanentDB.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/initiate-session")
public class SessionInitiationController {
    private final SessionInitiationServiceImpl sessionInitiationService;
    private final OfficeServicesImpl officeServicesImpl;

    public SessionInitiationController(SessionInitiationServiceImpl sessionInitiationService, OfficeServicesImpl officeServicesImpl) {
        this.sessionInitiationService = sessionInitiationService;
        this.officeServicesImpl = officeServicesImpl;
    }

    @PostMapping("/get-student-for-promotion")
    public ResponseEntity<List<StudentOnlyDTO>> getStudentForPromotion(@RequestBody SessionInitiationDTO sessionInitiationDTO) {
        return ResponseEntity.ok(sessionInitiationService.getStudentsForPromotion(sessionInitiationDTO));
    }
    @PostMapping("/initiate")
    public ResponseEntity<Boolean> testNewSession(@RequestBody SessionInitiationData sessionInitiationData){
        return ResponseEntity.ok(sessionInitiationService.initiateNewSession(sessionInitiationData));
    }


//    @GetMapping("/student-to-promote")
//    public ResponseEntity<Stu>

}
