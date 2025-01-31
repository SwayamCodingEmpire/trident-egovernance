package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.StudentOnlyDTO;
import com.trident.egovernance.global.entities.views.Attendance;
import com.trident.egovernance.global.entities.views.FeeCollectionView;
import com.trident.egovernance.global.entities.views.Results;
import com.trident.egovernance.global.entities.views.RollSheet;
import com.trident.egovernance.global.helpers.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    private Courses course;
    @Column(name = "BRANCH_CODE")
    private String branchCode;
    @Column(name = "ADMISSIONYEAR")
    private String admissionYear;
    @Column(name = "DEGREE_YOP")
    private Integer degreeYop;
    @Column(name = "PHNO")
    @Size(max = 255)
    private String phNo;
    @Column(name = "EMAIL")
    @Size(max = 255)
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
    @Enumerated(EnumType.STRING)
    private StudentStatus status;
    @Column(name = "BATCHID")
    @Size(max = 255)
    private String batchId;
    @Column(name = "CURRENTYEAR")
    @Min(0)
    private Integer currentYear;
    @Column(name = "AADHAARNO")
    @Min(0)
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
    @Column(name = "MSUSERPRINCIPALNAME", unique = true)
    private String msUserPrincipalName;
    //    @Column(name = "SECTIONID")
//    private Long sectionId;
//    @Column(name = "SEMESTER")
//    private Integer semester;
    // Constructor to initialize Student from StudentOnlyDTO
    public Student(StudentOnlyDTO studentOnlyDTO) {
        this.regdNo = studentOnlyDTO.regdNo();
        this.studentName = studentOnlyDTO.studentName();
        this.gender = studentOnlyDTO.gender();
        this.dob = studentOnlyDTO.dob();
        this.course = studentOnlyDTO.course();
        this.branchCode = studentOnlyDTO.branchCode();
        this.admissionYear = studentOnlyDTO.admissionYear();
        this.degreeYop = studentOnlyDTO.degreeYop();
        this.phNo = studentOnlyDTO.phNo();
        this.email = studentOnlyDTO.email();
        this.studentType = studentOnlyDTO.studentType();
        this.hostelier = studentOnlyDTO.hostelier();
        this.transportAvailed = studentOnlyDTO.transportAvailed();
        this.status = studentOnlyDTO.status();
        this.batchId = studentOnlyDTO.batchId();
        this.currentYear = studentOnlyDTO.currentYear();
        this.aadhaarNo = studentOnlyDTO.aadhaarNo();
        this.indortrng = studentOnlyDTO.indortrng();
        this.plpoolm = studentOnlyDTO.plpoolm();
        this.cfPayMode = studentOnlyDTO.cfPayMode();
        this.religion = studentOnlyDTO.religion();
        this.msUserPrincipalName = (studentOnlyDTO.msUserPrincipalName() == null ? "N/A" : studentOnlyDTO.msUserPrincipalName());
    }


//    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
//    private List<DuesDetails> duesDetailsList;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    private StudentCareer studentCareer;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    private StudentAdmissionDetails studentAdmissionDetails;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PersonalDetails personalDetails;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Hostel hostel;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Transport transport;
    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private RollSheet rollSheet;

    @OneToOne(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Roll_Sheet roll_Sheet;

    @OneToOne(mappedBy = "student") // Reference the Results entity
    private Results results;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<FeeCollection> feeCollection;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<FeeCollectionView> feeCollectionViews;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<StudentDocs> studentDocs;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Attendance> attendances;
}