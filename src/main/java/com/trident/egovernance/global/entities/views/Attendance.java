package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.AttendanceId;
import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Entity(name = "ATTENDANCE")
@Table(name = "ATTENDANCE")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AttendanceId.class)
public class Attendance {
    @Id
    @Column(name = "REGDNO", nullable = false, length = 255)
    private String regdNo;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "COURSE", length = 255)
    @Convert(converter = CourseConverter.class)
    private Courses course;

    @Column(name = "BRANCH", length = 255)
    private String branch;

    @Column(name = "SEM", precision = 2)
    private Integer sem;

    @Column(name = "SECTION", length = 40)
    private String section;

    @Column(name = "SUB_CODE", length = 20)
    private String subCode;

    @Column(name = "SUBJECT_NAME", length = 100)
    private String subjectName;

    @Id
    @Column(name = "SUB_ABBR", length = 15)
    private String subAbbr;

    @Column(name = "CLASSID", nullable = false, length = 150)
    private String classId;

    @Column(name = "TOTALCLASSES")
    private Integer totalClasses;

    @Column(name = "TOTALATTENDED")
    private Integer totalAttended;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO", insertable = false, updatable = false)
    @ToString.Exclude
    private Student student;
}
