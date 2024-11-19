package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapping for DB view
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Immutable
@Table(name = "COLLECTIONREPORT")
public class CollectionReport {

    @Column(name = "REGDNO")
    private String regdno;
    @Column(name = "NAME")
    private String name;
    @Column(name = "REFNO")
    private Long refno;
    @Id
    @Column(name = "MRNO")
    private Long mrNo;
    @Column(name = "PAYMENT_DATE")
    private String paymentDate;
    @Column(name = "CURRENTYEAR")
    private Integer currentyear;
    @Column(name = "PAYMENTMODE")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentmode;
    @Column(name = "DDNO")
    private String ddno;
    @Column(name = "DDDATE")
    private String dddate;
    @Column(name = "DDBANK")
    private String ddbank;
    @Column(name = "COLLECTEDFEE")
    private BigDecimal collectedfee;
}