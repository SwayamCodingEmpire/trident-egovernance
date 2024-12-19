package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.SGPADTO;
import com.trident.egovernance.global.entities.views.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ResultsRepository extends JpaRepository<Results, String> {
    @Query("""
    SELECT new com.trident.egovernance.dto.SGPADTO(
        CAST(COALESCE(MAX(r.sgpa1), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa2), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa3), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa4), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa5), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa6), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa7), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa8), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.cgpa), 0) AS bigdecimal)
    )
    FROM Results r
    LEFT JOIN STUDENT s ON r.student.regdNo = s.regdNo
        WHERE s.course = (
        SELECT s2.course
        FROM STUDENT s2
        WHERE s2.regdNo = :regdNo
    )
    AND s.currentYear = (
        SELECT s2.currentYear
        FROM STUDENT s2
        WHERE s2.regdNo = :regdNo
    )
    GROUP BY s.branchCode, s.currentYear
""")
    SGPADTO findMaxSgpasByCourseAndYear(String regdNo);
    @Query(""" 
    SELECT new com.trident.egovernance.dto.SGPADTO(
        CAST(COALESCE(AVG(r.sgpa1), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa2), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa3), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa4), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa5), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa6), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa7), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa8), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.cgpa), 0) AS bigdecimal)
    )
    FROM Results r
    LEFT JOIN STUDENT s ON r.student.regdNo = s.regdNo
    WHERE s.course = (
        SELECT s2.course 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    ) 
    AND s.currentYear = (
        SELECT s2.currentYear 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    )
    GROUP BY s.branchCode, s.currentYear
""")
    SGPADTO findAvgSgpasByCourseAndYear(String regdNo);


    @Query("""
    SELECT new com.trident.egovernance.dto.SGPADTO(
        CAST(COALESCE(MAX(r.sgpa1), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa2), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa3), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa4), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa5), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa6), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa7), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.sgpa8), 0) AS bigdecimal),
        CAST(COALESCE(MAX(r.cgpa), 0) AS bigdecimal)
    )
    FROM Results r
    LEFT JOIN STUDENT s ON r.student.regdNo = s.regdNo
    WHERE s.branchCode = (
        SELECT s2.branchCode 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    ) 
    AND s.currentYear = (
        SELECT s2.currentYear 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    )
    GROUP BY s.branchCode, s.currentYear
""")
    SGPADTO findMaxSgpasByBranchAndYear(String regdNo);
    @Query(""" 
    SELECT new com.trident.egovernance.dto.SGPADTO(
        CAST(COALESCE(AVG(r.sgpa1), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa2), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa3), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa4), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa5), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa6), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa7), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.sgpa8), 0) AS bigdecimal),
        CAST(COALESCE(AVG(r.cgpa), 0) AS bigdecimal)
    )
    FROM Results r
    LEFT JOIN STUDENT s ON r.student.regdNo = s.regdNo
    WHERE s.branchCode = (
        SELECT s2.branchCode 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    ) 
    AND s.currentYear = (
        SELECT s2.currentYear 
        FROM STUDENT s2 
        WHERE s2.regdNo = :regdNo
    )
    GROUP BY s.branchCode, s.currentYear
""")
    SGPADTO findAvgSgpasByBranchAndYear(String regdNo);


    Optional<SGPADTO> findByRegdNo(String regdNo);

    @Query("SELECT r.cgpa FROM Results r WHERE r.regdNo = :regdNo")
    Optional<BigDecimal> findCGPAByRegdNo(String regdNo);
}
