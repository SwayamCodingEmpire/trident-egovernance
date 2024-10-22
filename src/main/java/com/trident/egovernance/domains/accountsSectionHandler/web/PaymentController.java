package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.domains.accountsSectionHandler.DiscountAndAdjustmentService;
import com.trident.egovernance.domains.accountsSectionHandler.PaymentProcessingServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts-section/payment")
public class PaymentController {
    private final PaymentProcessingServices paymentProcessingService;
    private final DiscountAndAdjustmentService discountAndAdjustmentService;

    public PaymentController(PaymentProcessingServices paymentProcessingService, DiscountAndAdjustmentService discountAndAdjustmentService) {
        this.paymentProcessingService = paymentProcessingService;
        this.discountAndAdjustmentService = discountAndAdjustmentService;
    }
    @PostMapping("/testPayment/{no}")
    public ResponseEntity<MrDetailsSorted> testPayment(@RequestBody FeeCollection feeCollection, @PathVariable String no){
        return ResponseEntity.ok(paymentProcessingService.processPayment(feeCollection,no));              // Process the payment
    }

    @PostMapping("/insertDiscountData")
    public ResponseEntity<Boolean> insertDiscountData(@RequestBody Discount discount){
        return ResponseEntity.ok(discountAndAdjustmentService.insertDiscountData(discount));
    }
    @PostMapping("/applyAdjustment")
    public ResponseEntity<Adjustments> applyAdjustment(@RequestBody Adjustments adjustments){
        return ResponseEntity.ok(discountAndAdjustmentService.addAdjustment(adjustments));
    }
}
