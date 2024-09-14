package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.Courses;
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
    @Enumerated(EnumType.STRING)
    private Courses course;
    private Integer startYear;
    private Integer duration;
}
