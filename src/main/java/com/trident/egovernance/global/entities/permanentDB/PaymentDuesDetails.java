package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name  = "PAYMENTDUESDETAILS")
@Table(name  = "PAYMENTDUESDETAILS")
public class PaymentDuesDetails {
    @Id
    private Long mrNo;
    @Column(name = "ARREARS")
    private BigDecimal arrears;
    @Column(name = "CURRENTDUES")
    private BigDecimal currentDues;
    @Column(name = "TOTALPAID")
    private BigDecimal totalPaid;
    @Column(name = "AMOUNTDUE")
    private BigDecimal amountDue;

    @ToString.Exclude
    @OneToOne
    @MapsId
    @JoinColumn(name = "MRNO")
    private FeeCollection feeCollection;

    public PaymentDuesDetails(com.trident.egovernance.dto.PaymentDuesDetails paymentDuesDetails) {
        this.arrears = paymentDuesDetails.arrears();
        this.currentDues = paymentDuesDetails.currentDues();
        this.totalPaid = paymentDuesDetails.totalPaid();
        this.amountDue = paymentDuesDetails.amountDue();
    }
}
