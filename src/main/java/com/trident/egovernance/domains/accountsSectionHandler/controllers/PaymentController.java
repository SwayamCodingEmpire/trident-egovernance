package com.trident.egovernance.domains.accountsSectionHandler.controllers;

import com.trident.egovernance.domains.accountsSectionHandler.services.AccountSectionService;
import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.AlterFeeCollection;
import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.domains.accountsSectionHandler.services.DiscountAndAdjustmentService;
import com.trident.egovernance.domains.accountsSectionHandler.services.PaymentProcessingServices;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.PDFGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts-section/payment")
@Tag(name = "Accounts Section - Payment APIs", description = "APIs accessible by users with role ACCOUNTS and ADMIN only")
public class PaymentController {
    private final PDFGenerationService pdfGeneration;
    private final PaymentProcessingServices paymentProcessingService;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final DiscountAndAdjustmentService discountAndAdjustmentService;
    private final AccountSectionService accountSectionService;
    private final StudentRepository studentRepository;
    private final EmailSenderServiceImpl emailSenderServiceImpl;

    public PaymentController(PDFGenerationService pdfGeneration, PaymentProcessingServices paymentProcessingService, DiscountAndAdjustmentService discountAndAdjustmentService, AccountSectionService accountSectionService, StudentRepository studentRepository, EmailSenderServiceImpl emailSenderServiceImpl) {
        this.pdfGeneration = pdfGeneration;
        this.paymentProcessingService = paymentProcessingService;
        this.discountAndAdjustmentService = discountAndAdjustmentService;
        this.accountSectionService = accountSectionService;
        this.studentRepository = studentRepository;
        this.emailSenderServiceImpl = emailSenderServiceImpl;
    }


    @Operation(summary = "Insert discount for a student", description = "Directly insert value into Discount entity. Returns true if successfull. Complete logic is in a database trigger")
    @PostMapping("/insert-Discount-Data")
    public ResponseEntity<Boolean> insertDiscountData(@RequestBody Discount discount){
        List<GrantedAuthority> newAuthorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        discount.setStaffId(newAuthorities.get(2).toString());
        return ResponseEntity.ok(discountAndAdjustmentService.insertDiscountData(discount));
    }
    @Operation(summary = "Insert discount for a student", description = "Directly insert value into Adjustment entity. Returns adjustment if successfull. Complete logic is in a database trigger")
    @PostMapping("/apply-Adjustment")
    public ResponseEntity<Adjustments> applyAdjustment(@RequestBody Adjustments adjustments){
        List<GrantedAuthority> newAuthorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        adjustments.setStaffId(newAuthorities.get(2).toString());
        return ResponseEntity.ok(discountAndAdjustmentService.addAdjustment(adjustments));
    }

//    @PostMapping("/course-dues-payment/{no}")
//    public ResponseEntity<MoneyReceipt> processWithCourseFeePriority(@RequestBody FeeCollection feeCollection, @PathVariable String no){
//        return ResponseEntity.ok(paymentProcessingService.processNonAutoModes(feeCollection,no));
//    }

    @GetMapping("/get-new-mrNo")
    public ResponseEntity<Long> getNewMrNo(){
        return ResponseEntity.ok(paymentProcessingService.getMaxMrNo());
    }

    @Operation(summary = "Fee Collection endpoint", description = "Insert into FeeCollection Entity along with path variable of regdNo")
    @PostMapping("/fees-payment/{regdNo}")
    public ResponseEntity<MoneyReceipt> feesPayment(@RequestBody FeeCollection feeCollection, @PathVariable("regdNo") String regdNo, @RequestHeader("oboToken") String oboToken){
        logger.info("feesPayment");
        return ResponseEntity.ok(paymentProcessingService.processPaymentInterface(feeCollection,regdNo, oboToken.substring(7),false, null));
    }

//    @GetMapping("/fees-payment/{mrNo}")
//    public ResponseEntity<FeeCollection> feesPayment(@PathVariable("mrNo") Long mrNo){
//        return ResponseEntity.ok(accountSectionService.getFeeCollectionBeMrNo(mrNo));
//    }

//    @PutMapping("/update-fee-collection")
//    public ResponseEntity<MoneyReceipt> updateFeeCollection(@RequestBody FeeCollection feeCollection){
//        logger.info("updateFeeCollection");
//        return ResponseEntity.ok(paymentProcessingService.updateFeesCollection(feeCollection));// Process the payment
//    }

    @Operation(summary = "Delete Collection endpoint", description = "Takes mrNo as path variable to delete that mrNo record from FeeCollection and MrDetails Table")
    @DeleteMapping("/delete-fee-collection/{mrNo}")
    public ResponseEntity<Void> deleteFeeCollection(@PathVariable("mrNo") Long mrNo){
        if (paymentProcessingService.deleteFeeCollection(mrNo)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if deletion failed
        }
    }

    @Operation(summary = "Other Fee Collection endpoint", description = "Insert into Other FeeCollection Entity along with path variable of regdNo")
    @PostMapping("/other-fees-payment/{regdNo}")
    public ResponseEntity<MoneyReceipt> otherFeesPayment(@RequestBody OtherFeesPayment otherFeesPayment, @PathVariable("regdNo") String regdNo, @RequestHeader("oboToken") String oboToken){
        logger.info("otherFeesPayment");
        logger.info(otherFeesPayment.toString());
        return ResponseEntity.ok(paymentProcessingService.processOtherFessPaymentInterface(otherFeesPayment,regdNo, false, oboToken.substring(7), null));
    }

    @PostMapping("/accept-fee-collection-edit")
    public ResponseEntity<MoneyReceipt> editFeeCollection(@RequestBody AlterFeeCollection feeCollection, @RequestHeader("oboToken") String oboToken){
        return ResponseEntity.ok(paymentProcessingService.acceptPaymentEditRequest(feeCollection, oboToken));
    }
}
