package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.entities.permanentDB.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Table(name = "RESULTS")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class Results {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Column(name = "SGPA1")
    private BigDecimal sgpa1;
    @Column(name = "SGPA2")
    private BigDecimal sgpa2;
    @Column(name = "SGPA3")
    private BigDecimal sgpa3;
    @Column(name = "SGPA4")
    private BigDecimal sgpa4;
    @Column(name = "SGPA5")
    private BigDecimal sgpa5;
    @Column(name = "SGPA6")
    private BigDecimal sgpa6;
    @Column(name = "SGPA7")
    private BigDecimal sgpa7;
    @Column(name = "SGPA8")
    private BigDecimal sgpa8;
    @Column(name = "CGPA")
    private BigDecimal cgpa;
    @Column(name = "BACKLOGS")
    private BigDecimal backlogs;

    @OneToOne
    @JoinColumn(name = "REGDNO", referencedColumnName = "REGDNO") // Map the REGDNO in Student
    @ToString.Exclude
    private Student student;
}
