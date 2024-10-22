package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Date;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STUDENTDOCS")
@Table(name = "STUDENTDOCS")
public class StudentDocs implements Serializable {
    @Id
    @Column(name = "DOCID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "student_docs_seq")
    @SequenceGenerator(name = "student_docs_seq",sequenceName = "studentdocs_seq",allocationSize = 1)
    private Integer docId;
//    @Column(name = "REGDNO")
//    private String regdNo;
    @Column(name = "DOCLINK")
    private String docLink;
    @Column(name = "DOCTYPE")
    private String docType;
    @Column(name = "UPLOADDATE")
    private Date uploadDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    @ToString.Exclude
    private Student student;
}
