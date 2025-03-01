package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.SessionIdId;
import com.trident.egovernance.global.helpers.StudentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(SessionIdId.class)
@Entity(name = "TEST_TABLE")
@Table(name = "TEST_TABLE")
public class TestTable {
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "STARTDATE")
    private Date startDate;
    @Column(name = "ENDDATE")
    private Date endDate;
    @Column(name = "COURSE")
    @Id
    private String course;
    @Column(name = "REGDYEAR")
    @Id
    private int regdYear;
    @Column(name = "PREVSESSIONSID",length = 10)
    private String prevSessionId;
    @Column(name = "ADMISSIONYEAR")
    @Id
    private int admissionYear;
    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    public TestTable(TestTable testTable) {
        this.sessionId = testTable.getSessionId();
        this.startDate = testTable.getStartDate();
        this.endDate = testTable.getEndDate();
        this.course = testTable.getCourse();
        this.regdYear = testTable.getRegdYear();
        this.prevSessionId = testTable.getPrevSessionId();
        this.admissionYear = testTable.getAdmissionYear();
        this.studentType = testTable.getStudentType();
    }
}
