package com.trident.egovernance.global.entities.permanentDB;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trident.egovernance.dto.OtherFeeCollection;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
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
    @Transient
    private FeeProcessingMode feeProcessingMode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    @JsonIgnore
    private Student student;
    @ToString.Exclude
    @OneToMany(mappedBy = "feeCollection",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MrDetails> mrDetails;
    public FeeCollection(OtherFeeCollection otherFeeCollection) {
        this.collectedFee = otherFeeCollection.collectedFee();
        this.paymentMode = otherFeeCollection.paymentMode();
    }
}
