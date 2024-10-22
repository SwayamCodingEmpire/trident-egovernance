package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract sealed class BaseDuesDetails implements Serializable permits DuesDetails, OldDueDetails {
    @Id
    @Column(name = "REGDNO")
    protected String regdNo;
    @Id
    @Column(name = "DESCRIPTION", length = 15)
    protected String description;
    @Column(name = "AMOUNTDUE", precision = 10, scale = 2)
    private BigDecimal amountDue;
    @Column(name = "AMOUNTPAID")
    private BigDecimal amountPaid;
    @Column(name = "BALAMT")
    private BigDecimal balanceAmount;
    @Column(name = "DEDUCTIONORDER")
    private Integer deductionOrder;
    @Column(name = "DUEYEAR")
    private Integer dueYear;
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "AMOUNTPAIDTOJEE")
    private BigDecimal amountPaidToJee;
    @Column(name = "DUEDATE")
    private Date dueDate;
    @Column(name = "DISCOUNT")
    private BigDecimal discount;
    @Column(name = "ADJUSTMENT")
    private BigDecimal adjustment;

    public BaseDuesDetails(BaseDuesDetails base) {
        this.regdNo = base.regdNo;
        this.description = base.description;
        this.amountDue = base.amountDue;
        this.amountPaid = base.amountPaid;
        this.balanceAmount = base.balanceAmount;
        this.deductionOrder = base.deductionOrder;
        this.dueYear = base.dueYear;
        this.sessionId = base.sessionId;
        this.amountPaidToJee = base.amountPaidToJee;
        this.dueDate = base.dueDate;
        this.discount = base.discount;
        this.adjustment = base.adjustment;
    }
}
