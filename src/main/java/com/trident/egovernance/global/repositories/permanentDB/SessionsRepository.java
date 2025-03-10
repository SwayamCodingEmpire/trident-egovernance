package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.SessionWiseRecords;
import com.trident.egovernance.global.entities.permanentDB.Course;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.SessionIdId;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.helpers.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SessionsRepository extends JpaRepository<Sessions, SessionIdId> {
    Sessions findBySessionIdAndCourseAndRegdYearAndStudentType(String sessionId, String course, int regdYear, String studentType);
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("UPDATE SESSIONS s " +
            "SET s.endDate = :endDate " +
            "WHERE s.course = :course AND " +
            "s.regdYear = :regdyear AND " +
            "s.admissionYear = :admissionYear AND " +
            "s.studentType = :studentType")
    int updateSessionsForEndingSession(Date endDate, String course, int regdyear, String studentType, int admissionYear);

    @Query("SELECT DISTINCT(s.sessionId) FROM SESSIONS s")
    Set<String> findAllSessionIds();

//    @Query("""
//    SELECT new com.trident.egovernance.dto.SessionWiseRecords(
//        se.session,
//        se.course,
//        br.branch,
//        se.studentType,
//        se.regdYear,
//        COUNT(CASE WHEN s.gender = 'MALE' THEN 1 ELSE NULL END),
//        COUNT(CASE WHEN s.gender = 'FEMALE' THEN 1 ELSE NULL END),
//        COUNT(s)
//    )
//    FROM SESSIONS se
//    LEFT JOIN BRANCH br
//      ON se.course = br.course
//    LEFT JOIN STUDENT s
//      ON s.branchCode = br.branchCode
//      AND s.course = br.course
//      AND CAST(s.admissionYear AS int) = CAST(se.admissionYear AS int)
//      AND s.studentType = se.studentType
//      AND s.currentYear = se.regdYear
//      AND s.status = :status
//    GROUP BY se.session, se.course, br.branch, se.studentType, se.regdYear
//""")
//    List<SessionWiseRecords> fetchSessionWiseStatistics(StudentStatus status);

    @Query("SELECT s FROM SESSIONS s WHERE s.course = :course AND s.regdYear = :regdYear " +
            "AND s.admissionYear = :admissionYear AND s.studentType = :studentType AND s.endDate IS NULL")
    Optional<Sessions> findByCourseAndRegdYearAndAdmissionYearAndStudentType(String course, int regdYear, int admissionYear, String studentType);

    List<Sessions> findAllByCourseOrderBySessionIdAscRegdYearAsc(String course);

    @Query("SELECT DISTINCT s.sessionId FROM SESSIONS s WHERE s.course = :course AND s.regdYear = :regdYear AND s.endDate IS NULL")
    List<String> findAllByCourseAndRegdyear(Integer regdYear, String course);

    @Query("SELECT s FROM SESSIONS s WHERE s.regdYear <> :regdYear AND s.endDate IS NULL ORDER BY s.regdYear ASC")
    List<Sessions> findAllByAndRegdYearEndDateIsNull(int regdYear);
}
