package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STUDENT")
@Table(name = "STUDENT")
public class Student {

    @Column(name = "REGDNO")
    @Id
    private String regdNo;
    @Column(name = "NAME")
    private String studentName;
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "DOB")
    private String dob;
    @Convert(converter = CourseConverter.class)
    @Column(name = "COURSE")
    private CoursesEnum course;
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
    @ToString.Exclude
    private StudentCareer studentCareer;
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private StudentAdmissionDetails studentAdmissionDetails;
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PersonalDetails personalDetails;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private Hostel hostel;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private Transport transport;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<FeeCollection> feeCollection;

}
