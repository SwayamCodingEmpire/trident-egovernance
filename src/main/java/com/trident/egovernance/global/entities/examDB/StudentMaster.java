package com.trident.egovernance.global.entities.examDB;

import com.trident.egovernance.dto.StudentMasterDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "SUBCODE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentMaster {
    @Id
    @Column(name = "SUBCODE", length = 15)
    String subcode;

    @Column(name = "SUBJECTNAME", length = 100)
    String subjectName;

    @Column(name = "CREDIT")
    Integer credit;

    @Column(name = "SEMESTER")
    String semester;

    @Column(name = "BRANCH", length = 70)
    String branch;

    public StudentMaster(StudentMasterDTO studentMasterDTO) {
        this.subcode = studentMasterDTO.subcode();
        this.subjectName = studentMasterDTO.subjectName();
        this.credit = studentMasterDTO.credit();
        this.semester = studentMasterDTO.semester();
        this.branch = studentMasterDTO.branch();
    }
}
