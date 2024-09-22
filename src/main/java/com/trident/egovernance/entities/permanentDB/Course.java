package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.CoursesEnum;
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

    public CoursesEnum getCourse() {
        return CoursesEnum.fromDisplayName(course);
    }

    public void setCourse(CoursesEnum course) {
        this.course = course.getDisplayName();
    }
}
