package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.entities.permanentDB.Subject_Details;
import com.trident.egovernance.global.helpers.SemesterResultId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "SEMESTERRESULT")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@IdClass(SemesterResultId.class)
public class SemesterResult {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Id
    @Column(name = "SEMESTER")
    private int semester;
    @Id
    @Column(name = "SUBJECTCODE")
    private String subjectCode;
    @Column(name = "GRADE")
    private String grade;
    @Column(name = "CREDITS")
    private int credits;
    @Column(name = "RESPUBDATE")
    private String resPubDate;

    @ManyToOne
    @JoinColumn(name = "SUBJECTCODE", referencedColumnName = "SUBJECTCODE", insertable = false, updatable = false)
    private Subject_Details subjectDetails;
}
