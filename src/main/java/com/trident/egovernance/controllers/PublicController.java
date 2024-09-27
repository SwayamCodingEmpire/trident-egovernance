package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dto.CurrentSessionDto;
import com.trident.egovernance.dto.Login;
import com.trident.egovernance.dto.LoginResponse;
import com.trident.egovernance.repositories.permanentDB.FeesRepository;
import com.trident.egovernance.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.services.AuthenticationServiceImpl;
import com.trident.egovernance.services.CustomJwtServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final AuthenticationServiceImpl authenticationService;
    @PersistenceContext
    private final EntityManager entityManager;
    private final StudentRepository studentRepository;
    private final CustomJwtServiceImpl customJwtService;
    private final Logger logger = LoggerFactory.getLogger(PublicController.class);
    private final FeesRepository feesRepository;

    public PublicController(AuthenticationServiceImpl authenticationService, EntityManager entityManager, StudentRepository studentRepository, CustomJwtServiceImpl customJwtService,
                            FeesRepository feesRepository) {
        this.authenticationService = authenticationService;
        this.entityManager = entityManager;
        this.studentRepository = studentRepository;
        this.customJwtService = customJwtService;
        this.feesRepository = feesRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login login){
        logger.info(login.toString());
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticationService.authenticate(login);
        logger.info(customUserDetails.toString());
        return ResponseEntity.ok(new LoginResponse(customJwtService.generateToken(customUserDetails), customJwtService.getExpirationTime()));
    }

    @GetMapping("/count")
    public long count(){
        return feesRepository.count();
    }
    @GetMapping("/view")
    public ResponseEntity<List<CurrentSessionDto>> view(){
        String sql = "SELECT SESSIONID " +
                "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("regdNo","REGD1234");
        return query.getResultList().isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(query.getResultList());
    }
}
