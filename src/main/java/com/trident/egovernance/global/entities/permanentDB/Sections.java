package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "SECTIONS")
@Table(name = "SECTIONS")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Sections {
    @Id
    @Column(name = "SECTIONID")
    private Long sectionId;
    @Column(name = "ADMISSIONYEAR")
    private String admissionYear;
    @Column(name = "COURSE")
    @Convert(converter = CourseConverter.class)
    private Courses course;
    @Column(name = "BRANCHCODE", length = 15)
    private String branchCode;

    @Column(name = "SECTION", nullable = false, length = 20)
    private String section;

    @Column(name = "SEM", precision = 4)
    private Integer sem;

    @Column(name = "CURRENTYEAR", precision = 5)
    private Integer currentYear;

    // Foreign key relationship to "Branch" entity
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "BRANCHCODE", referencedColumnName = "BRANCHCODE", insertable = false, updatable = false),
            @JoinColumn(name = "COURSE", referencedColumnName = "COURSE", insertable = false, updatable = false)
    })
    private Branch branch;
    @OneToMany(mappedBy = "section")
    @ToString.Exclude
    private List<Student> students;
}
