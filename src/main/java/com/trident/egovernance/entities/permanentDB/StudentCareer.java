package com.trident.egovernance.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STUDENT_CAREER")
@Table(name = "STUDENT_CAREER")
public class StudentCareer {

    @Id
    @Column(name = "REGDNO", length = 15)
    private String regdNo;  // VARCHAR2(15)

    @Column(name = "TENTHPERCENTAGE", precision = 7, scale = 2)
    private BigDecimal tenthPercentage;  // NUMBER(7,2)

    @Column(name = "TENTHYOP", precision = 20)
    private Long tenthYOP;  // NUMBER(20,0)

    @Column(name = "TWELVTHPERCENTAGE", precision = 7, scale = 2)
    private BigDecimal twelvthPercentage;  // NUMBER(7,2)

    @Column(name = "TWELVTHYOP", precision = 20)
    private Long twelvthYOP;  // NUMBER(20,0)

    @Column(name = "DIPLOMAPERCENTAGE", precision = 7, scale = 2)
    private BigDecimal diplomaPercentage;  // NUMBER(7,2)

    @Column(name = "DIPLOMAYOP", precision = 20)
    private Long diplomaYOP;  // NUMBER(20,0)

    @Column(name = "GRADUATIONPERCENTAGE", precision = 7, scale = 2)
    private BigDecimal graduationPercentage;  // NUMBER(7,2)

    @Column(name = "GRADUATIONYOP", precision = 20)
    private Long graduationYOP;  // NUMBER(20,0)

    @OneToOne
    @JoinColumn(name = "REGDNO")
    @MapsId
    private Student student;  // One-to-One relationship with Student
}

