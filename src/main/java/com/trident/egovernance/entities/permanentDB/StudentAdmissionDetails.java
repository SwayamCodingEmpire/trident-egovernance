package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.TFWType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STUDENT_ADMISSION_DETAILS")
@Table(name = "STUDENT_ADMISSION_DETAILS")
public class StudentAdmissionDetails {
    @Id
    @Column(name = "REGDNO", length = 12)
    private String regdNo;  // VARCHAR2(12)

    @Column(name = "ADMISSIONDATE")
    private Date admissionDate;  // DATE

    @Column(name = "OJEECOUNSELLINGFEEPAID", length = 5)
    private String ojeeCounsellingFeePaid;  // VARCHAR2(5)

    @Enumerated(EnumType.STRING)
    @Column(name = "TFW", length = 5)
    private TFWType tfw;  // VARCHAR2(5)

    @Column(name = "ADMISSIONTYPE", length = 20)
    private String admissionType;  // VARCHAR2(20)

    @Column(name = "OJEEROLLNO", length = 20)
    private String ojeeRollNo;  // VARCHAR2(20)

    @Column(name = "OJEERANK", length = 20)
    private String ojeeRank;  // VARCHAR2(20)

    @Column(name = "AIEEERANK", length = 20)
    private String aieeeRank;  // VARCHAR2(20)

    @Column(name = "CASTE", length = 20)
    private String caste;  // VARCHAR2(20)

    @Column(name = "REPORTINGDATE")
    @Temporal(TemporalType.DATE)
    private Date reportingDate;  // DATE

    @Column(name = "CATEGORYCODE", length = 10)
    private String categoryCode;  // VARCHAR2(10)

    @Column(name = "CATEGORYRANK", precision = 10)
    private Long categoryRank;  // NUMBER(10,0)

    @Column(name = "JEEAPPLICATIONNO", length = 20)
    private String jeeApplicationNo;  // VARCHAR2(20)

    @Column(name = "ALLOTMENTID", length = 20)
    private String allotmentId;

    @OneToOne
    @JoinColumn(name = "REGDNO")
    @MapsId
    private Student student;
}
