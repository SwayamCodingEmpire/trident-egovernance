package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.helpers.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    @Query(value = "SELECT REGDNO, COURSE, CURRENTYEAR, SESSIONID, PREVSESSIONSID, STARTDATE " +
            "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo", nativeQuery = true)
    List<CurrentSessionDto> getCurrentSessionDtoByRegdNo(@Param("regdNo") String regdNo);

    List<StudentOfficeDTO> findAllByStatus(StudentStatus status);

    List<StudentOfficeDTO> findAllByCourse(Courses course);

    BasicStudentDto findByRegdNo(String regdNo);

    Long countAllByStatus(StudentStatus status);
    List<Student> findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(String admissionYear, Courses course, Integer currentYear, StudentType studentType);
    List<Student> findAllByCourseAndCurrentYear(Courses course, Integer currentYear);
    @Query("SELECT DISTINCT s FROM STUDENT s " +
    "LEFT JOIN FETCH STUDENT_ADMISSION_DETAILS " +
    "LEFT JOIN FETCH PERSONAL_DETAILS " +
    "LEFT JOIN FETCH STUDENT_CAREER " +
    "WHERE s.regdNo IN :regdNo")
    List<Student> findAllByRegdNo(@Param("regdNo") List<String> regdNos);

    @Query(value = """
SELECT DISTINCT
        b.COURSE AS course,
        b.BRANCHCODE AS branchCode,
        NVL(COUNT(s.REGDNO), 0) AS studentCount
    FROM
        BRANCH b 
    LEFT JOIN
        STUDENT s
    ON s.COURSE = b.COURSE AND s.BRANCH_CODE = b.BRANCHCODE
    WHERE b.STUDENTTYPE=:studentType AND s.STATUS=:status
    GROUP BY b.COURSE, b.BRANCHCODE
    ORDER BY b.COURSE, b.BRANCHCODE
""", nativeQuery = true)
    List<Object[]> findRawStudentCountsGroupedByCourseAndBranch(@Param("studentType") String studentType, @Param("status") String status);

    @Query("SELECT s FROM STUDENT s WHERE s.batchId LIKE :batchId AND s.status=:status")
    List<Student> findAllByBatchIdAndStatus(@Param("batchId") String batchId, @Param("status") StudentStatus status);

}
