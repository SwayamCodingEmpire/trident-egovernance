package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.SectionFetcher;
import com.trident.egovernance.dto.StudentSectionData;
import com.trident.egovernance.global.entities.views.RollSheet;
import com.trident.egovernance.global.helpers.CourseConverter;
import com.trident.egovernance.global.helpers.Courses;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity(name = "SECTIONS")
@Table(name = "SECTIONS")
public class Sections {
    @Id
    @Column(name = "SECTIONID")
    private Long sectionId;
    @Column(name = "COURSE")
//    @Convert(converter = CourseConverter.class)
    private String course;
    @Column(name = "BRANCHCODE", length = 15)
    private String branchCode;

    @Column(name = "SECTION", nullable = false, length = 20)
    private String section;

    @Column(name = "SEM", precision = 4)
    private Integer sem;


    // Foreign key relationship to "Branch" entity
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "BRANCHCODE", referencedColumnName = "BRANCHCODE", insertable = false, updatable = false),
            @JoinColumn(name = "COURSE", referencedColumnName = "COURSE", insertable = false, updatable = false)
    })
    private Branch branch;

    @OneToMany(mappedBy = "sections", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Roll_Sheet> rollSheets;

    public Sections(SectionFetcher sectionFetcher) {
        this.branchCode = sectionFetcher.branchCode();
        this.course = sectionFetcher.course().getDisplayName();
        this.section = sectionFetcher.section();
        this.sem = sectionFetcher.sem();
        List<Roll_Sheet> newStudentSectionData = new ArrayList<>();
        Roll_Sheet rollSheet;
        for(StudentSectionData studentSectionData : sectionFetcher.studentSectionData()) {
            rollSheet = new Roll_Sheet(studentSectionData);
            newStudentSectionData.add(rollSheet);
        }
        this.rollSheets = newStudentSectionData;
    }
}