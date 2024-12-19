package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.entities.views.SemesterResult;
import com.trident.egovernance.global.services.SubjectTempDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "SUBJECT_DETAILS")
@Table(name = "SUBJECT_DETAILS")
public class Subject_Details {
    @Id
    @Column(name = "SLNO")
    private Long serialNumber;

    @Column(name = "SUBJECTCODE", length = 50)
    private String subjectCode;

    @Column(name = "SUBJECTNAME", nullable = false, length = 255)
    private String subjectName;

    @Column(name = "COURSE", nullable = false, length = 50)
    private String course;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "SEMESTER", nullable = false)
    private Integer semester;

    // Transient PDF link field that doesn't map to a column in the database
    @Transient
    private String pdfLink;

    @OneToMany(mappedBy = "subjectDetails", fetch = FetchType.LAZY)
    private List<SemesterResult> semesterResults;

    public Subject_Details(SubjectTempDTO subjectTempDTO) {
        this.serialNumber = subjectTempDTO.getSerialNumber();
        this.subjectCode = subjectTempDTO.getSubjectCode();
        this.subjectName = subjectTempDTO.getSubjectName();
        this.course = subjectTempDTO.getCourse();
        this.year = Integer.valueOf(subjectTempDTO.getYear());
        this.semester = Integer.valueOf(subjectTempDTO.getSemester());
        this.pdfLink = subjectTempDTO.getPdfLink();
    }
}
