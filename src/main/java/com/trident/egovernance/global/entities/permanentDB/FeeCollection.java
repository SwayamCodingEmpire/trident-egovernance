package com.trident.egovernance.global.entities.permanentDB;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trident.egovernance.dto.OtherFeeCollection;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "FEECOLLECTION")
@Table(name = "FEECOLLECTION")
public class FeeCollection {
    @Id
    @Column(name = "MRNO")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mrno_seq_gen")
//    @SequenceGenerator(name = "mrno_seq_gen", sequenceName = "MRNO_SEQ", allocationSize = 1)
    private Long mrNo;
//    @Column(name = "REGDNO",insertable = false,updatable = false)
//    private String regdNo;
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
    @Column(name = "DUEYEAR")
    private int dueYear;
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "PAYMENTRECEIVER")
    private String paymentReceiver;
    @Enumerated(EnumType.STRING)
    private FeeProcessingMode feeProcessingMode;
    @Transient
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    @JsonIgnore
    private Student student;
    @ToString.Exclude
    @OneToMany(mappedBy = "feeCollection",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<MrDetails> mrDetails;

    @ToString.Exclude
    @OneToOne(mappedBy = "feeCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentDuesDetails paymentDuesDetails;
    public FeeCollection(OtherFeeCollection otherFeeCollection) {
        this.collectedFee = otherFeeCollection.collectedFee();
        this.paymentMode = otherFeeCollection.paymentMode();
        this.feeProcessingMode = FeeProcessingMode.NA;
    }

    public FeeCollection(AlterFeeCollection alterFeeCollection) {
        this.mrNo = alterFeeCollection.getMrNo();
        this.collectedFee = alterFeeCollection.getCollectedFee();
        this.paymentMode = alterFeeCollection.getPaymentMode();
        this.feeProcessingMode = alterFeeCollection.getFeeProcessingMode();
        if(this.paymentMode.equals(PaymentMode.DD)){
            this.ddNo = alterFeeCollection.getDdNo();
            this.ddDate = alterFeeCollection.getDdDate();
            this.ddBank = alterFeeCollection.getDdBank();
        }
        this.paymentDate = alterFeeCollection.getPaymentDate();
        this.paymentReceiver = alterFeeCollection.getPaymentReceiver();
        this.feeProcessingMode = alterFeeCollection.getFeeProcessingMode();
        if(this.feeProcessingMode.equals(FeeProcessingMode.NA)){
            mrDetails = alterFeeCollection.getMrDetails();
        }
    }
}
