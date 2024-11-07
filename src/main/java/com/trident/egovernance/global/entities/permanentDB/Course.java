package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    @Enumerated(EnumType.STRING)
    @Column(name = "STUDENTTYPE")
    private StudentType studentType;
    public Courses getCourse() {
        return Courses.fromDisplayName(course);
    }

    public void setCourse(Courses course) {
        this.course = course.getDisplayName();
    }
}
