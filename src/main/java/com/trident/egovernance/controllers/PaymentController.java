package com.trident.egovernance.controllers;

import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.entities.permanentDB.Discount;
import com.trident.egovernance.entities.permanentDB.FeeCollection;
import com.trident.egovernance.services.PaymentProcessingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentProcessingServiceImpl paymentProcessingService;

    public PaymentController(PaymentProcessingServiceImpl paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }
    @PostMapping("/testPayment/{no}")
    public ResponseEntity<MrDetailsSorted> testPayment(@RequestBody FeeCollection feeCollection, @PathVariable String no){
        return ResponseEntity.ok(paymentProcessingService.processPayment(feeCollection,no));              // Process the payment
    }

    public ResponseEntity<Boolean> insertDiscountData(@RequestBody Discount discount){
        return ResponseEntity.ok(paymentProcessingService.insertDiscountData(discount));
    }
}
