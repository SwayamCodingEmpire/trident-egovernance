package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "ALTERFEECOLLECTION")
@Table(name = "ALTERFEECOLLECTION")
public class AlterFeeCollection {
    @Id
    @Column(name = "REFNO")
    private Long refNo;
    @Column(name = "MRNO")
    private Long mrNo;
    @Column(name = "REGDNO")
    private String regdNo;
    @Column(name = "COLLECTEDFEE")
    private BigDecimal collectedFee;
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENTMODE")
    private PaymentMode paymentMode;
    @Column(name = "DDNO")
    private String ddNo;
    @Column(name = "DDDATE")
    private String ddDate;
    @Column(name = "DDBANK")
    private String ddBank;
    @Column(name = "PAYMENT_DATE")
    private String paymentDate;
    @Column(name = "PAYMENTRECEIVER")
    private String paymentReceiver;
    @Column(name = "COMMENTS")
    private String comments;
}


