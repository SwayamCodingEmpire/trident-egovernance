package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class BaseAdjustments {
    private String regdNo;
    private String description;
    private BigDecimal actualDueAmount;
    private BigDecimal considerationAmount;
    private String approvedBy;
    private String sessionId;
    private int regdYear;
    private String staffId;
}
