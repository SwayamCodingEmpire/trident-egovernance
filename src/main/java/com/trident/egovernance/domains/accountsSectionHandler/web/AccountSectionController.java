package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts-section")
public class AccountSectionController {
    private final AccountSectionService accountSectionService;
    private final FeeCollectionRepository feeCollectionRepository;

    public AccountSectionController(AccountSectionService accountSectionService, FeeCollectionRepository feeCollectionRepository) {
        this.accountSectionService = accountSectionService;
        this.feeCollectionRepository = feeCollectionRepository;
    }

    @GetMapping("/getDuesDetails/{regdNo}")
    public ResponseEntity<Map<Integer, Map<Integer, List<DuesDetailsDto>>>> getDuesDetails(@PathVariable("regdNo") String regdNo){
        return ResponseEntity.ok(accountSectionService.getDuesDetails(regdNo));
    }

    @GetMapping("/get-basic-student-details/{regdNo}")
    public ResponseEntity<BasicStudentDto> getBasicStudentDetails(@PathVariable("regdNo") String regdNo){
        return ResponseEntity.ok(accountSectionService.getBasicStudentDetails(regdNo));
    }

    @GetMapping("/get-fee-collection-history/{regdNo}")
    public ResponseEntity<FeeCollectionHistoryDto> getFeeCollectionHistory(@PathVariable("regdNo") String regdNo){
        return ResponseEntity.ok(accountSectionService.getFeeCollectionByRegdNo(regdNo));
    }

    @GetMapping("/get-fee-collection-by-sessionId/{sessionId}")
    public ResponseEntity<List<FeeCollectionOnlyDTO>> getFeeCollectionBySessionId(@PathVariable("sessionId") String sessionId){
        return ResponseEntity.ok(accountSectionService.getFeeCollectionBySessionId(sessionId));
    }

    @GetMapping("/get-dashboard-data/{paymentDate}")
    public ResponseEntity<FeeDashboardSummary> getFeeDashboardData(@PathVariable("paymentDate") String paymentDate){
        return ResponseEntity.ok(accountSectionService.getDashBoardNumbers(paymentDate));
    }
}
