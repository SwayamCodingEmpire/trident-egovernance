package com.trident.egovernance.entities.redisEntities;

import com.trident.egovernance.helpers.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@RedisHash("NSR")
public class NSR implements Serializable {
    //Student
    @Id
    private String jeeApplicationNo;
    private String regdNo;
    private Date admissionDate;
    private BooleanString ojeeCouncellingFeePaid;
    private String studentName;
    private Gender gender;
    private String branchCode;
    private String admissionYear;
    private Integer degree_yop;
    private String phNo;
    private String email;
    private String rollNo;
    private BooleanString hostelier;
    private BooleanString hostelOption;
    private HostelChoice hostelChoice;
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
    private CoursesEnum course;
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
    private String fname;  // VARCHAR2(100)
    private String mname;  // VARCHAR2(100)
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
    private BooleanString ojeeCounsellingFeePaid;  // VARCHAR2(5)
    private String ojeeRollNo;  // VARCHAR2(20)
    private String ojeeRank;  // VARCHAR2(20)
    private String aieeeRank;  // VARCHAR2(20)
    private String caste;  //
    private java.util.Date reportingDate;  //
    private String categoryCode;  // VARCHAR2(10)
    private Long categoryRank;  // NUMBER(10,0)
    private String allotmentId;

    private List<StudentDocData> studentDocsData;

    private BooleanString transportOpted;
    private String pickUpPoint;
    private Integer step;
}
