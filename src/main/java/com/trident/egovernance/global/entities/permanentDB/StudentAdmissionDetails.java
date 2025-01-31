package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.StudentAdmissionDetailsOnlyDTO;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.TFWType;
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
    @Enumerated(EnumType.STRING)
    private BooleanString ojeeCounsellingFeePaid;  // VARCHAR2(5)

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

    public StudentAdmissionDetails(StudentAdmissionDetailsOnlyDTO dto) {
        this.regdNo = dto.regdNo();
        this.admissionDate = dto.admissionDate();
        this.ojeeCounsellingFeePaid = dto.ojeeCounsellingFeePaid();
        this.tfw = dto.tfw();
        this.admissionType = dto.admissionType();
        this.ojeeRollNo = dto.ojeeRollNo();
        this.ojeeRank = dto.ojeeRank();
        this.aieeeRank = dto.aieeeRank();
        this.caste = dto.caste();
        this.reportingDate = dto.reportingDate();
        this.categoryCode = dto.categoryCode();
        this.categoryRank = dto.categoryRank();
        this.jeeApplicationNo = dto.jeeApplicationNo();
        this.allotmentId = dto.allotmentId();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "REGDNO")
    @MapsId
    private Student student;
}