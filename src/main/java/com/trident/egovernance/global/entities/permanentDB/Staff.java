package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.StaffDetailsDto;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "STAFFDETAILS")
@Table(name = "STAFFDETAILS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Staff {
    @Id
    @Column(name = "STAFF_ID")
    private Long staffId;

    @Column(name = "STAFF_NAME", nullable = false, length = 30)
    private String staffName;

    @Column(name = "STAFF_DEPT", nullable = false, length = 15)
    private String staffDept;

    @Column(name = "STAFF_DESIGNATION", nullable = false, length = 50)
    private String staffDesignation;

    @Column(name = "STAFF_CATEGORY", nullable = false, length = 20)
    private String staffCategory;

    @Column(name = "STATUS", nullable = false, length = 15)
    private String status;

    @Column(name = "ROLE", nullable = false, length = 20)
    private String role;

    @Column(name = "USERNAME", nullable = false, length = 30)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 20)
    private String password;

    @Column(name = "PHNO", nullable = false, length = 300)
    private String phoneNumber;

    @Column(name = "ADDRESS", nullable = false, length = 500)
    private String address;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "SECQ", nullable = false, length = 150)
    private String securityQuestion;

    @Column(name = "SECANS", nullable = false, length = 150)
    private String securityAnswer;

    @Column(name = "COLLEGE_NAME", nullable = false, length = 20)
    private String collegeName;

    public Staff(StaffDetailsDto staff) {
        this.staffId = staff.staffId();
        this.staffName = staff.staffName();
        this.staffDept = staff.staffDept();
        this.staffDesignation = staff.staffDesignation();
        this.staffCategory = staff.staffCategory();
        this.status = staff.status();
        this.role = staff.role();
        this.username = staff.username();
        this.password = staff.password();
        this.phoneNumber = staff.phoneNumber();
        this.address = staff.address();
        this.email = staff.email();
        this.securityQuestion = staff.securityQuestion();
        this.securityAnswer = staff.securityAnswer();
        this.collegeName = staff.collegeName();
    }
}
