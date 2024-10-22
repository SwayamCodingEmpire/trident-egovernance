package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MappedSuperclass
public sealed abstract class BaseDiscount permits Discount, OldDiscount {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "REGDNO")
    private String regdNo;
    @Column(name = "DISCOUNT", precision = 9, scale = 2)
    private BigDecimal discount;
    @Column(name = "STAFFID")
    private String staffId;
    @Column(name = "PARTICULARS")
    private String particulars;
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "REGDYEAR")
    private Integer regdYear;
    @Column(name = "COMMENTS")
    private String comments;
}
