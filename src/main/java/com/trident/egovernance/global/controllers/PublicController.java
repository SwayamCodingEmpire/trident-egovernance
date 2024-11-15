package com.trident.egovernance.global.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dto.CurrentSessionDto;
import com.trident.egovernance.dto.Login;
import com.trident.egovernance.dto.LoginResponse;
import com.trident.egovernance.global.entities.permanentDB.BaseDuesDetails;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeesRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.AuthenticationServiceImpl;
import com.trident.egovernance.global.services.CustomJwtServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final DuesDetailsRepository duesDetailsRepository;
    private final AuthenticationServiceImpl authenticationService;
    @PersistenceContext
    private final EntityManager entityManager;
    private final StudentRepository studentRepository;
    private final CustomJwtServiceImpl customJwtService;
    private final Logger logger = LoggerFactory.getLogger(PublicController.class);
    private final FeesRepository feesRepository;

    public PublicController(DuesDetailsRepository duesDetailsRepository, AuthenticationServiceImpl authenticationService, EntityManager entityManager, StudentRepository studentRepository, CustomJwtServiceImpl customJwtService,
                            FeesRepository feesRepository) {
        this.duesDetailsRepository = duesDetailsRepository;
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

    @GetMapping("/getDuesDetails")
    public ResponseEntity<List<DuesDetails>> getDuesDetails(){
        DuesDetails duesDetails = new DuesDetails();
        duesDetails.setRegdNo("12345");
        duesDetails.setDueYear(1);
        duesDetails.setDeductionOrder(1);
        duesDetails.setDueDate(new java.sql.Date(System.currentTimeMillis()));
        duesDetails.setAmountDue(BigDecimal.valueOf(1000));
        duesDetails.setAmountPaid(BigDecimal.ZERO);
        duesDetails.setBalanceAmount(BigDecimal.ONE);
        duesDetails.setSessionId("2022-2023");
        duesDetails.setAmountPaidToJee(BigDecimal.ZERO);
        duesDetails.setDescription("DEMO");
        duesDetailsRepository.save(duesDetails);
        List<DuesDetails> testDueDetails = duesDetailsRepository.findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder("12345", BigDecimal.ZERO);
        logger.info(testDueDetails.toString());
        BaseDuesDetails baseDuesDetails = testDueDetails.getFirst();
        logger.info(baseDuesDetails.toString());
        return ResponseEntity.ok(testDueDetails);
    }
}
