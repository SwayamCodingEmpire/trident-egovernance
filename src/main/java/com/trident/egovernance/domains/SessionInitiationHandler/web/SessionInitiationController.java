package com.trident.egovernance.domains.SessionInitiationHandler.web;

import com.trident.egovernance.domains.SessionInitiationHandler.services.SessionInitiationServiceImpl;
import com.trident.egovernance.dto.SessionInitiationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/initiate-session")
public class SessionInitiationController {
    private final SessionInitiationServiceImpl sessionInitiationService;

    public SessionInitiationController(SessionInitiationServiceImpl sessionInitiationService) {
        this.sessionInitiationService = sessionInitiationService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<Boolean> testNewSession(@RequestBody SessionInitiationDTO sessionInitiationDTO){
        return ResponseEntity.ok(sessionInitiationService.initiateNewSession(sessionInitiationDTO));
    }
}
