package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.CfPaymentMode;
import com.trident.egovernance.global.helpers.TFWType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "FEES")
@Table(name = "FEES")
public class Fees implements Serializable {
    @Id
    private Long feeId;
    private String batchId;
    private Integer regdYear;
    @Column(insertable = false,updatable = false)
    private String description;
    private BigDecimal amount;
    private String comments;
    @Enumerated(EnumType.STRING)
    private TFWType tfwType;
    private BigDecimal tatFees;
    private BigDecimal tactFfees;
    @Enumerated(EnumType.STRING)
    private CfPaymentMode payType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESCRIPTION")
    private FeeTypes feeType;
}
