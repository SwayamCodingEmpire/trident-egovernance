package com.trident.egovernance.controllers;

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
    public ResponseEntity<String> testPayment(@RequestBody FeeCollection feeCollection, @PathVariable String no){
//        paymentProcessingService.processPayment(feeCollection,no);
        return ResponseEntity.ok("Processed");
    }
}
