package com.trident.egovernance.domains.accountsSectionHandler.web;

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

@RestController
@RequestMapping("/accounts-section/payment")
public class PaymentController {
    private final PaymentProcessingServices paymentProcessingService;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final DiscountAndAdjustmentService discountAndAdjustmentService;

    public PaymentController(PaymentProcessingServices paymentProcessingService, DiscountAndAdjustmentService discountAndAdjustmentService) {
        this.paymentProcessingService = paymentProcessingService;
        this.discountAndAdjustmentService = discountAndAdjustmentService;
    }


    @PostMapping("/insertDiscountData")
    public ResponseEntity<Boolean> insertDiscountData(@RequestBody Discount discount){
        return ResponseEntity.ok(discountAndAdjustmentService.insertDiscountData(discount));
    }
    @PostMapping("/applyAdjustment")
    public ResponseEntity<Adjustments> applyAdjustment(@RequestBody Adjustments adjustments){
        return ResponseEntity.ok(discountAndAdjustmentService.addAdjustment(adjustments));
    }

//    @PostMapping("/course-dues-payment/{no}")
//    public ResponseEntity<MoneyReceipt> processWithCourseFeePriority(@RequestBody FeeCollection feeCollection, @PathVariable String no){
//        return ResponseEntity.ok(paymentProcessingService.processNonAutoModes(feeCollection,no));
//    }

    @PostMapping("/fees-payment/{no}")
    public ResponseEntity<MoneyReceipt> feesPayment(@RequestBody FeeCollection feeCollection, @PathVariable String no){
        logger.info("feesPayment");
        if(feeCollection.getFeeProcessingMode().equals(FeeProcessingMode.AUTO)){
            return ResponseEntity.ok(paymentProcessingService.processAutoPayment(feeCollection,no));
        }
        else{
            return ResponseEntity.ok(paymentProcessingService.processNonAutoModes(feeCollection,no));
        }// Process the payment
    }

    @PostMapping("/other-fees-payment/{regdNo}")
    public ResponseEntity<MoneyReceipt> otherFeesPayment(@RequestBody OtherFeesPayment otherFeesPayment, @PathVariable String regdNo){
        return ResponseEntity.ok(paymentProcessingService.processOtherFeesPayment(otherFeesPayment,regdNo));
    }
}
