package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.sql.Date;

@Entity
@Table(name = "CURRENT_SESSION")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class CurrentSession {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Convert(converter = CourseConverter.class)
    @Column(name = "COURSE")
    private Courses course;
    @Column(name = "CURRENTYEAR")
    private int currentYear;
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "PREVSESSIONSID")
    private String prevSessionsId;
    @Column(name = "STARTDATE")
    private Date startDate;
}
