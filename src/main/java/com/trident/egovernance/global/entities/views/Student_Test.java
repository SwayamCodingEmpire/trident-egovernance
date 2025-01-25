package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.helpers.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity(name = "STUDENT_TEST")
@Table(name = "STUDENT_TEST")
@Getter
@Setter
@Immutable
@NoArgsConstructor
public class Student_Test {
    @Id
    @Column(name = "REGDNO", nullable = false, length = 255)
    private String regdNo;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "GENDER", length = 255)
    private String gender;

    @Column(name = "DOB", length = 255)
    private String dob;

    @Column(name = "COURSE", length = 255)
    @Convert(converter = CourseConverter.class)
    private Courses course;

    @Column(name = "BRANCH_CODE", length = 255)
    private String branchCode;

    @Column(name = "ADMISSIONYEAR", length = 20)
    private String admissionYear;

    @Column(name = "DEGREE_YOP")
    private Long degreeYop;

    @Column(name = "PHNO", length = 255)
    private String phone;

    @Column(name = "EMAIL", length = 255)
    private String email;

    @Column(name = "STUDENTTYPE", length = 50)
    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    @Column(name = "HOSTELIER", length = 50)
//    @Enumerated(EnumType.STRING)
    private String hostelier;

    @Column(name = "TRANSPORTAVAILED", length = 50)
//    @Enumerated(EnumType.STRING)
    private String transportAvailed;

    @Column(name = "STATUS", length = 255)
    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(name = "BATCHID", length = 255)
    private String batchId;

    @Column(name = "CURRENTYEAR")
    private Integer currentYear;

    @Column(name = "AADHAARNO")
    private Long aadhaarNo;

    @Column(name = "INDORTRNG", length = 20)
//    @Enumerated(EnumType.STRING)
    private String indOrTrng;

    @Column(name = "PLPOOLM", length = 20)
//    @Enumerated(EnumType.STRING)
    private String plPoolM;

    @Column(name = "CFPAYMODE", length = 20)
    private String cfPayMode;

    @Column(name = "RELIGION", length = 20)
    private String religion;

    public Student_Test(String regdNo, String name, String gender, String dob, Courses course, String branchCode,
                   String admissionYear, Long degreeYop, String phone, String email, StudentType studentType,
                   String hostelier, String transportAvailed, StudentStatus status, String batchId, Integer currentYear,
                   Long aadhaarNo, String indOrTrng, String plPoolM, String cfPayMode, String religion) {
        this.regdNo = regdNo;
        this.name = name;
        this.gender = gender == null ? "MALE" : gender.toUpperCase();
        this.dob = dob;
        this.course = course;
        this.branchCode = branchCode;
        this.admissionYear = admissionYear;
        this.degreeYop = degreeYop;
        this.phone = phone;
        this.email = email;
        this.studentType = studentType;
        this.hostelier = hostelier == null ? "YES" : hostelier.toUpperCase();
        this.transportAvailed = transportAvailed == null ? "YES" : transportAvailed.toUpperCase();
        this.status = status;
        this.batchId = batchId;
        this.currentYear = currentYear;
        this.aadhaarNo = aadhaarNo;
        this.indOrTrng = indOrTrng == null ? "YES" : indOrTrng.toUpperCase();
        this.plPoolM = plPoolM == null ? "YES" : plPoolM.toUpperCase();
        this.cfPayMode = cfPayMode;
        this.religion = religion == null ? "HINDU" : religion.toUpperCase();
    }

    public Student_Test(Student_Test student_Test) {
        this.regdNo = student_Test.getRegdNo();
        this.name = student_Test.getName();
        this.gender = student_Test.getGender() == null ? "MALE" : student_Test.getGender().toUpperCase();
        this.dob = student_Test.getDob();
        this.course = student_Test.getCourse();
        this.branchCode = student_Test.getBranchCode();
        this.admissionYear = student_Test.getAdmissionYear();
        this.degreeYop = student_Test.getDegreeYop();
        this.phone = student_Test.getPhone();
        String sanitizedName = student_Test.getName() == null ? "" : student_Test.getName().replaceAll("\\s+", "");
        this.email = student_Test.getEmail() == null ? sanitizedName+student_Test.getRegdNo()+"@gmail.com" : student_Test.getEmail();
        this.studentType = student_Test.getStudentType();
        this.hostelier = student_Test.getHostelier() == null ? "YES" : student_Test.getHostelier().toUpperCase();
        this.transportAvailed = student_Test.getTransportAvailed() == null ? "YES" : student_Test.getTransportAvailed().toUpperCase();
        this.status = student_Test.getStatus();
        this.batchId = student_Test.getBatchId();
        this.currentYear = student_Test.getCurrentYear();
        this.aadhaarNo = student_Test.getAadhaarNo() == null ? 809345634598L : student_Test.getAadhaarNo();
        this.indOrTrng = student_Test.getIndOrTrng() == null ? "YES" : student_Test.getIndOrTrng().toUpperCase();
        this.plPoolM = student_Test.getPlPoolM() == null ? "YES" : student_Test.getPlPoolM().toUpperCase();
        this.cfPayMode = student_Test.getCfPayMode() == null ? "YEARLY" : student_Test.getCfPayMode().toUpperCase();
        this.religion = student_Test.getReligion() == null ? "HINDU" : student_Test.getReligion().toUpperCase();
    }

}
