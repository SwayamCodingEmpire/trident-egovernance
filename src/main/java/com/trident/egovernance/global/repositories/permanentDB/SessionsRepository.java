package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Course;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.SessionIdId;
import com.trident.egovernance.global.helpers.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Set;

@Repository
public interface SessionsRepository extends JpaRepository<Sessions, SessionIdId> {
    Sessions findBySessionIdAndCourseAndRegdYearAndStudentType(String sessionId, String course, int regdYear, String studentType);
    @Modifying
    @Query("UPDATE SESSIONS s " +
            "SET s.endDate = :endDate " +
            "WHERE s.sessionId = :sessionId AND " +
            "s.course = :course AND " +
            "s.regdYear = :regdyear AND " +
            "s.studentType = :studentType")
    int updateSessionsForEndingSession(Date endDate, String sessionId, String course, int regdyear, String studentType);

    @Query("SELECT DISTINCT(s.sessionId) FROM SESSIONS s")
    Set<String> findAllSessionIds();

}
