package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.dto.BasicStudentDto;
import com.trident.egovernance.dto.DuesDetailsSortedDto;
import com.trident.egovernance.dto.FeeCollectionHistoryDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts-section")
public class AccountSectionController {
    private final AccountSectionService accountSectionService;

    public AccountSectionController(AccountSectionService accountSectionService) {
        this.accountSectionService = accountSectionService;
    }

    @GetMapping("/getDuesDetails/{regdNo}")
    public DuesDetailsSortedDto getDuesDetails(@PathVariable("regdNo") String regdNo){
        return accountSectionService.getDuesDetails(regdNo);
    }

    @GetMapping("/get-basic-student-details/{regdNo}")
    public BasicStudentDto getBasicStudentDetails(@PathVariable("regdNo") String regdNo){
        return accountSectionService.getBasicStudentDetails(regdNo);
    }

    @GetMapping("/get-fee-collection-history/{regdNo}")
    public FeeCollectionHistoryDto getFeeCollectionHistory(@PathVariable("regdNo") String regdNo){
        return accountSectionService.getFeeCollectionByRegdNo(regdNo);
    }
}
