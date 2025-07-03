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
public class SubjectMaster {
    @Id
    @Column(name = "SUBJECTCODE", length = 15)
    String subjectCode;

    @Column(name = "SUBJECTNAME", length = 100)
    String subjectName;

    @Column(name = "CREDIT")
    Integer credit;

    @Column(name = "SEMESTER")
    String semester;

    @Column(name = "BRANCH", length = 70)
    String branch;

    public SubjectMaster(StudentMasterDTO studentMasterDTO) {
        this.subjectCode = studentMasterDTO.subjectCode();
        this.subjectName = studentMasterDTO.subjectName();
        this.credit = studentMasterDTO.credit();
        this.semester = studentMasterDTO.semester();
        this.branch = studentMasterDTO.branch();
    }
}
