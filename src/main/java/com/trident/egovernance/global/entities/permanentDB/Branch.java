package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.BranchId;
import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(BranchId.class)
@Entity(name = "BRANCH")
@Table(name = "BRANCH")
public class Branch {
    @Id
    @Column(name = "BRANCHCODE")
    private String branchCode;
    @Column(name = "BRANCH")
    private String branch;
    @Id
    @Column(name = "COURSE")
//    @Convert(converter = CourseConverter.class)
    private String course;
    @Column(name = "COURSEINPROGRESS")
    private Integer courseInProgress;
    @OneToMany(mappedBy = "branch")
    @ToString.Exclude
    private List<Sections> sections;

//    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
//    private List<Student> students;
}
