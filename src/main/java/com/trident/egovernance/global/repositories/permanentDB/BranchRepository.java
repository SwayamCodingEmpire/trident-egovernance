package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.AdmissionData;
import com.trident.egovernance.dto.BranchGroup;
import com.trident.egovernance.dto.BranchRecord;
import com.trident.egovernance.dto.TotalAdmissionData;
import com.trident.egovernance.global.entities.permanentDB.Branch;
import com.trident.egovernance.global.helpers.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, BranchId> {
//    @Query("""
//    SELECT new com.trident.egovernance.dto.AdmissionData(
//        b.course,
//        b.branchCode,
//        s.studentType,
//        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'GENERAL' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'GENERAL' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'OBC' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'OBC' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'SC' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'SC' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'ST' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'ST' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND s.religion != :hindu THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND s.religion != :hindu THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND sa.tfw = :tfw THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.tfw = :tfw  THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male AND sa.tfw = :ntfw THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female AND sa.tfw = :ntfw  THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :male THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = :female THEN 1 ELSE NULL END),
//        COUNT(s)
//    )
//    FROM BRANCH b
//    LEFT JOIN STUDENT s ON b.course = s.course AND b.branchCode = s.branchCode
//    LEFT JOIN s.studentAdmissionDetails sa WHERE s.admissionYear = :admissionYear
//    GROUP BY b.course, b.branchCode, s.studentType
//""")
//    List<AdmissionData> getStudentSummary(Gender male, Gender female, Religion hindu, TFWType tfw, TFWType ntfw, String admissionYear);
//
//    @Query("""
//    SELECT new com.trident.egovernance.dto.TotalAdmissionData(
//        COALESCE(s.admissionYear, ''),
//        b.course,
//        b.branchCode,
//        COALESCE(s.studentType, NULL),
//        COUNT(s.regdNo)
//    )
//    FROM BRANCH b
//    LEFT JOIN STUDENT s
//      ON b.course = s.course
//      AND b.branchCode = s.branchCode
//    WHERE b.course = :course
//      AND b.branchCode = :branch
//    GROUP BY s.admissionYear, b.course, b.branchCode, s.studentType
//""")
//    List<TotalAdmissionData> getAdmissionSummaryByCourseAndBranch(Courses course, String branch);
//

//    @Query("SELECT new com.trident.egovernance.dto.BranchGroup(b.course, " +
//            "(SELECT b1 FROM BRANCH b1 WHERE b1.course = b.course)) " +
//            "FROM BRANCH b " +
//            "GROUP BY b.course ORDER BY b.course")
//    List<BranchGroup> findAllBranchCode();

    @Query("SELECT new com.trident.egovernance.dto.BranchRecord(b.branchCode, b.branch, b.course, b.courseInProgress) FROM BRANCH b")
    List<BranchRecord> findAllNow();



}
