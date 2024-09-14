package com.trident.egovernance.dtos;

import com.trident.egovernance.annotations.ValidSize;
import com.trident.egovernance.helpers.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NSRDto implements Serializable {

    private String jeeApplicationNo;
    private String regdNo;
    private Date admissionDate;
    private BooleanString ojeeCouncellingFeePaid;
    private String studentName;
    private Gender gender;
    private String branchCode;
    private String branch;
    private String admissionYear;
    private Integer degree_yop;
    private String phNo;
    private String email;
    private String rollNo;
    private BooleanString hostelier;
    private BooleanString transportAvailed;
    private String status;
    private String batchId;
    private Integer currentYear;
    private Long aadhaarNo;
    private BooleanString indortrng;
    private BooleanString plpoolm;
    private CfPaymentMode cfPayMode;
    private Religion religion;
    private Long rank;
    private RankType rankType;
    private Courses course;
    private TFWType tfw;
    private AdmissionType admissionType;
    private StudentType studentType;

    //StudentCareer
    private Double tenthPercentage;
    private Integer tenthYOP;
    private Double twelvthPercentage;
    private Integer twelvthYOP;
    private Double diplomaPercentage;
    private Integer diplomaYOP;
    private Double graduationPercentage;
    private Integer graduationYOP;

    //PersonalDetails
    private String fName;  // VARCHAR2(100)
    private String mName;  // VARCHAR2(100)
    private String lgName;  // VARCHAR2(100) - Assuming lgName refers to legal guardian name
    private String permanentAddress;  // VARCHAR2(500)
    private String permanentCity;  // VARCHAR2(50)
    private String permanentState;  // VARCHAR2(50)
    private Long permanentPincode;  // NUMBER(10,0)
    private String parentContact;  // VARCHAR2(50)
    private String parentEmailId;  // VARCHAR2(50)
    private String presentAddress;  // VARCHAR2(500)
    private String district;

    //StudentAdmissionDetails
    private String ojeeCounsellingFeePaid;  // VARCHAR2(5)
    private String ojeeRollNo;  // VARCHAR2(20)
    private String ojeeRank;  // VARCHAR2(20)
    private String aieeeRank;  // VARCHAR2(20)
    private String caste;  //
    private java.util.Date reportingDate;  //
    private String categoryCode;  // VARCHAR2(10)
    private Long categoryRank;  // NUMBER(10,0)
    private String allotmentId;

    private Integer step;
}
