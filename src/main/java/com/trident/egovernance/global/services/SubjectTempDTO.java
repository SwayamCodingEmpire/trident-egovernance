package com.trident.egovernance.global.services;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectTempDTO {
    private Long serialNumber;
    private String subjectCode;
    private String subjectName;
    private String course;
    private String year;
    private String semester;
    // Transient PDF link field that doesn't map to a column in the database
    @Transient
    private String pdfLink;
}
