package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.entities.permanentDB.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity(name = "ROLLSHEET")
@Table(name = "ROLLSHEET")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class RollSheet {
    @Id
    @Column(name = "REGDNO", length = 20)
    private String regdNo;

    @Column(name = "SEM", nullable = false)
    private int sem;

    @Column(name = "SECTION", length = 40, nullable = false)
    private String section;

    @Column(name = "LABGROUP", nullable = false)
    private int labGroup;

    @Column(name = "COLLEGEROLLNO", nullable = false)
    private int collegeRollNo;

    @Column(name = "SESSIONID", length = 20, nullable = false)
    private String sessionId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    private Student student;
}
