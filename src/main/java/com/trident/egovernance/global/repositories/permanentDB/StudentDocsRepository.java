package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.StudentDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface StudentDocsRepository extends JpaRepository<StudentDocs,Integer> {

    @Modifying
    @Query("UPDATE STUDENTDOCS s SET s.docId=:docLink, s.docType = :docType, s.uploadDate = :uploadDate WHERE s.student.regdNo = :regdNo AND s.docId = :docId")
    void updateStudentDocs(String docLink, String docType, Date uploadDate,String regdNo, Integer docId);
}
