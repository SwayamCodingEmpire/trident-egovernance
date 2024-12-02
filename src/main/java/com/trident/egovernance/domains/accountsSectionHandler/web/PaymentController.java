package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.dto.MoneyReceipt;
import com.trident.egovernance.dto.OtherFeesPayment;
import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.domains.accountsSectionHandler.DiscountAndAdjustmentService;
import com.trident.egovernance.domains.accountsSectionHandler.PaymentProcessingServices;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/accounts-section/payment")
public class PaymentController {
    private final PaymentProcessingServices paymentProcessingService;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final DiscountAndAdjustmentService discountAndAdjustmentService;
    private final AccountSectionService accountSectionService;

    public PaymentController(PaymentProcessingServices paymentProcessingService, DiscountAndAdjustmentService discountAndAdjustmentService, AccountSectionService accountSectionService) {
        this.paymentProcessingService = paymentProcessingService;
        this.discountAndAdjustmentService = discountAndAdjustmentService;
        this.accountSectionService = accountSectionService;
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
        MoneyReceipt moneyReceipt = new MoneyReceipt();
        if(feeCollection.getFeeProcessingMode().equals(FeeProcessingMode.AUTO)){
            moneyReceipt = paymentProcessingService.processPaymentAutoMode(feeCollection,regdNo,false);
        }
        else{
            moneyReceipt = paymentProcessingService.processPaymentNonAutoModes(feeCollection,regdNo,false);
        }// P
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(moneyReceipt.getMrNo())
                .toUri();
        return ResponseEntity.created(location).body(moneyReceipt);
    }

//    @GetMapping("/fees-payment/{mrNo}")
//    public ResponseEntity<FeeCollection> feesPayment(@PathVariable("mrNo") Long mrNo){
//        return ResponseEntity.ok(accountSectionService.getFeeCollectionBeMrNo(mrNo));
//    }

    @PutMapping("/update-fee-collection")
    public ResponseEntity<MoneyReceipt> updateFeeCollection(@RequestBody FeeCollection feeCollection){
        logger.info("updateFeeCollection");
        return ResponseEntity.ok(paymentProcessingService.updateFeesCollection(feeCollection));// Process the payment
    }

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
