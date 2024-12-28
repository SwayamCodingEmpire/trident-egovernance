package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.domains.accountsSectionHandler.DiscountAndAdjustmentService;
import com.trident.egovernance.domains.accountsSectionHandler.PaymentProcessingServices;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.PDFGenerationService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

@RestController
@RequestMapping("/accounts-section/payment")
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


    @PostMapping("/insert-Discount-Data")
    public ResponseEntity<Boolean> insertDiscountData(@RequestBody Discount discount){
        return ResponseEntity.ok(discountAndAdjustmentService.insertDiscountData(discount));
    }
    @PostMapping("/apply-Adjustment")
    public ResponseEntity<Adjustments> applyAdjustment(@RequestBody Adjustments adjustments){
        return ResponseEntity.ok(discountAndAdjustmentService.addAdjustment(adjustments));
    }

//    @PostMapping("/course-dues-payment/{no}")
//    public ResponseEntity<MoneyReceipt> processWithCourseFeePriority(@RequestBody FeeCollection feeCollection, @PathVariable String no){
//        return ResponseEntity.ok(paymentProcessingService.processNonAutoModes(feeCollection,no));
//    }

    @PostMapping("/fees-payment/{regdNo}")
    public ResponseEntity<MoneyReceipt> feesPayment(@RequestBody FeeCollection feeCollection, @PathVariable("regdNo") String regdNo){
        logger.info("feesPayment");
        return ResponseEntity.ok(paymentProcessingService.processPaymentInterface(feeCollection,regdNo,false));
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

    @DeleteMapping("/delete-fee-collection/{mrNo}")
    public ResponseEntity<Void> deleteFeeCollection(@PathVariable("mrNo") Long mrNo){
        if (paymentProcessingService.deleteFeeCollection(mrNo)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if deletion failed
        }
    }

    @PostMapping("/other-fees-payment/{regdNo}")
    public ResponseEntity<MoneyReceipt> otherFeesPayment(@RequestBody OtherFeesPayment otherFeesPayment, @PathVariable("regdNo") String regdNo){
        logger.info("otherFeesPayment");
        logger.info(otherFeesPayment.toString());
        return ResponseEntity.ok(paymentProcessingService.processOtherFeesPayment(otherFeesPayment,regdNo, false));
    }
}
