package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.DuesDetailsId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(DuesDetailsId.class)
@Entity(name = "DUESDETAIL")
@Table(name = "DUESDETAIL")
public class DuesDetails {
    @Column(name = "ID")
    private Long id;
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Id
    @Column(name = "DESCRIPTION",length = 15)
    private String description;
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
}
