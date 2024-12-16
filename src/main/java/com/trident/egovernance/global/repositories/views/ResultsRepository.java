package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.SGPADTO;
import com.trident.egovernance.global.entities.views.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    WHERE s.regdNo = :regdNo
    GROUP BY r.student.course, r.student.currentYear
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
    WHERE s.regdNo = :regdNo
    GROUP BY r.student.course, r.student.currentYear
""")
    SGPADTO findAvgSgpasByCourseAndYear(String regdNo);

    Optional<SGPADTO> findByRegdNo(String regdNo);
}
