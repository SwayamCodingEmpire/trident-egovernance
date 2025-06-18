package com.trident.egovernance.global.entities.examDB;

import com.trident.egovernance.dto.StudentExamResultsDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "INSERTSEMESTERRESULT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamResults {

    @Id
    @Column(name = "REGDNO", length = 15)
    String regdno;

    @Column(name = "SEMESTER", length = 5)
    int semester;

    @Column(name = "SUBJECTCODE", length = 20)
    String subjectCode;

    @Column(name = "GRADE", length = 10)
    String grade;

    @Column(name = "CREDITS", length = 10)
    int credits;

    @Column(name = "RESPUBDATE", length = 20)
    String resultPublishDate;

    public StudentExamResults(StudentExamResultsDTO stud){
        this.regdno = stud.regdno();
        this.semester = stud.semester();
        this.subjectCode = stud.subjectCode();
        this.grade = stud.grade();
        this.credits = stud.credits();
        this.resultPublishDate = stud.resultPublishDate();
    }
}
