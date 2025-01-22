package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity(name = "STINROLL")
@Table(name = "STINROLL")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class StInRoll {
    @Id
    @Column(name = "REGDNO", nullable = false, length = 255)
    private String regdNo;

    @Column(name = "JEEAPPLICATIONNO", length = 20)
    private String jeeApplicationNo;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "BRANCH_CODE", length = 255)
    private String branchCode;

    @Column(name = "COURSE", length = 255)
    @Convert(converter = CourseConverter.class)
    private Courses course;

    @Column(name = "SECTION", length = 40)
    private String section;

    @Column(name = "LABGROUP", precision = 2)
    private Integer labGroup;

}
