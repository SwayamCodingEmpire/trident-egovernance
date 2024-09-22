package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    private Student student;
    @ToString.Exclude
    @OneToMany(mappedBy = "feeCollection",cascade = CascadeType.ALL)
    private List<MrDetails> mrDetails;
}
