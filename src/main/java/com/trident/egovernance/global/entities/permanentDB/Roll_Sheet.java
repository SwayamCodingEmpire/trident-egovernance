package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.StudentSectionData;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "ROLL_SHEET")
@Table(name = "ROLL_SHEET")
public class Roll_Sheet {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;

//    @Column(name = "SEM", nullable = false)
//    private int sem;

//    @Column(name = "SECTION", length = 40, nullable = false)
//    private String section;

    @Column(name = "LABGROUP", nullable = false)
    private int labGroup;

    @Column(name = "COLLEGEROLLNO", nullable = false)
    private int collegeRollNo;

//    @Column(name = "SECTIONID", nullable = false)
//    private Integer sectionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO", insertable = false, updatable = false)
    @ToString.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sectionId", referencedColumnName = "sectionId")
    private Sections sections;

    public Roll_Sheet(StudentSectionData studentSectionData) {
        this.labGroup = studentSectionData.labGroup();
        this.collegeRollNo = studentSectionData.collegeRollNo();
        this.regdNo = studentSectionData.regdNo();
    }
}
