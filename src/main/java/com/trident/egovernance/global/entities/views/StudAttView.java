package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.helpers.StudAttViewId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity(name = "STUDATTVIEW3")
@Table(name = "STUDATTVIEW3")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@IdClass(StudAttViewId.class)
public class StudAttView {
    @Id
    @Column(name = "REGDNO", nullable = false, length = 20)
    private String regdNo;  // Registration number

    @Column(name = "SEM", nullable = false, length = 5)
    private String sem;  // Semester

    @Id
    @Column(name = "SUB_CODE", nullable = false, length = 20)
    private String subCode;  // Subject code

    @Column(name = "SUBJECT", nullable = false, length = 117)
    private String subject;  // Subject name

    @Column(name = "CLASSESHELD", nullable = false)
    private Integer classesHeld;  // Total classes held

    @Column(name = "CLASSESATTENDED", nullable = false)
    private Integer classesAttended;
}
