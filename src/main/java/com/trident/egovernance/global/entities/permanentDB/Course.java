package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.CourseId;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(CourseId.class)
@Entity(name = "COURSE")
@Table(name = "COURSE")
public class Course {
    @Id
    @Column(name = "COURSE")
    private String course;
    @Column(name = "STARTYEAR")
    private Integer startYear;
    @Column(name = "DURATION")
    private Integer duration;
    @Transient
    private StudentType studentTypes;
    @Id
    @Column(name = "STUDENTTYPE")
    private String studentType;
    public Courses getCourse() {
        return Courses.fromDisplayName(course);
    }

    public void setCourse(Courses course) {
        this.course = course.getDisplayName();
    }

    public Course(String course, Integer startYear, Integer duration, StudentType studentTypes) {
        this.course = course;
        this.startYear = startYear;
        this.duration = duration;
        this.studentType = studentTypes.toString();
    }
}
