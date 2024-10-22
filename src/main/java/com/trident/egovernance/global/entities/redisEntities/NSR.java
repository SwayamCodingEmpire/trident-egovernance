package com.trident.egovernance.global.entities.redisEntities;

import com.trident.egovernance.global.helpers.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
    @NotNull(message = "studentName cannot be null")
    private String studentName;
    @NotNull(message = "gender cannot be null")
    private Gender gender;
    private String dob;
    @NotNull(message = "branchCode cannot be null")
    private String branchCode;
    @Indexed
    private String admissionYear;
    private Integer degreeYop;
    private String phNo;
    private String email;
    private String rollNo;
    private BooleanString hostelier;
    private BooleanString hostelOption;
    private HostelChoice hostelChoice;
    private BooleanString transportAvailed;
    private StudentStatus status;
    private String batchId;
    private Integer currentYear;
    private Long aadhaarNo;
    private BooleanString indortrng;
    private BooleanString plpoolm;
    private CfPaymentMode cfPayMode;
    private Religion religion;
    @NotNull(message = "Rank must not be Null")
    private Long rank;
    @NotNull(message = "rankType cannot be null")
    private RankType rankType;
    @NotNull(message = "course cannot be null")
    private Courses course;
    @NotNull(message = "TFWType cannot be null")
    private TFWType tfw;
    @NotNull(message = "admissionType cannot be null")
    private AdmissionType admissionType;
    @NotNull(message = "studentType cannot be null")
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
    private String lgName;  // VARCHAR2(100) - lgName refers to legal guardian name
    private String permanentAddress;  // VARCHAR2(500)
    private String permanentCity;  // VARCHAR2(50)
    private String permanentState;  // VARCHAR2(50)
    private Long permanentPincode;  // NUMBER(10,0)
    private String parentContact;  // VARCHAR2(50)
    private String parentEmailId;  // VARCHAR2(50)
    private String presentAddress;  // VARCHAR2(500)
    private String district;

    //StudentAdmissionDetails
    @NotNull(message = "ojeeCounsellingFeePaid cannot be null")
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
