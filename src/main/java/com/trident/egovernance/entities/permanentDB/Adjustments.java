package com.trident.egovernance.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "ADJUSTMENTS")
@Table(name = "ADJUSTMENTS")
public class Adjustments {
    @Id
    private Long id;
    private String regdNo;
    private String description;
    private BigDecimal actualDueAmount;
    private BigDecimal considerationAmount;
    private String approvedBy;
    private String sessionId;
    private int regdYear;
}
