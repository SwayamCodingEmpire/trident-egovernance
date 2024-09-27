package com.trident.egovernance.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "DISCOUNT")
@Table(name = "DISCOUNT")
public class Discount {
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
    @Transient
    private int currentYear;
}
