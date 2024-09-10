package com.trident.egovernance.entities.reportingStudent;

import com.trident.egovernance.helpers.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STUDENT")
@Table(name = "STUDENT")
public class Student {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Column(name = "NAME")
    private String studentName;
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "DOB")
    private String dob;
    @Column(name = "COURSE")
    @Enumerated(EnumType.STRING)
    private Courses course;
    @Column(name = "BRANCH_CODE")
    private String branchCode;
    @Column(name = "ADMISSIONYEAR")
    private String admissionYear;
    @Column(name = "DEGREE_YOP")
    private Integer degree_yop;
    @Column(name = "PHNO")
    private String phNo;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "STUDENTTYPE")
    @Enumerated(EnumType.STRING)
    private StudentType studentType;
    @Column(name = "HOSTELIER")
    @Enumerated(EnumType.STRING)
    private BooleanString hostelier;
    @Column(name = "TRANSPORTAVAILED")
    @Enumerated(EnumType.STRING)
    private BooleanString transportAvailed;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "BATCHID")
    private String batchId;
    @Column(name = "CURRENTYEAR")
    private Integer currentYear;
    @Column(name = "AADHAARNO")
    private Long aadhaarNo;
    @Column(name = "INDORTRNG")
    @Enumerated(EnumType.STRING)
    private BooleanString indortrng;
    @Column(name = "PLPOOLM")
    @Enumerated(EnumType.STRING)
    private BooleanString plpoolm;
    @Column(name = "CFPAYMODE")
    @Enumerated(EnumType.STRING)
    private CfPaymentMode cfPayMode;
    @Column(name = "RELIGION")
    @Enumerated(EnumType.STRING)
    private Religion religion;


    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentCareer studentCareer;
}
