package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.StudentDocs;

import java.sql.Date;

public record StudentDocsOnlyDTO(
        Integer docId,
        String docLink,
        String docType,
        Date uploadDate
) {
    public StudentDocsOnlyDTO(StudentDocs studentDocs) {
        this(
                studentDocs.getDocId(),
                studentDocs.getDocLink(),
                studentDocs.getDocType(),
                studentDocs.getUploadDate()
        );
    }
}
