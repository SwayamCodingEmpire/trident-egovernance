package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.CfPaymentMode;
import com.trident.egovernance.helpers.TFWType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "FEES")
@Table(name = "FEES")
public class Fees {
    @Id
    private Long feeId;
    private String batchId;
    private Integer regdYear;
    private String description;
    private BigDecimal amount;
    private String comments;
    @Enumerated(EnumType.STRING)
    private TFWType tfwType;
    private BigDecimal tatFees;
    private BigDecimal tactFfees;
    @Enumerated(EnumType.STRING)
    private CfPaymentMode payType;
}
