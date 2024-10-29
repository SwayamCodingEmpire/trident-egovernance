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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    @Query(value = "SELECT REGDNO, COURSE, CURRENTYEAR, SESSIONID, PREVSESSIONSID, STARTDATE " +
            "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo", nativeQuery = true)
    List<CurrentSessionDto> getCurrentSessionDtoByRegdNo(@Param("regdNo") String regdNo);

//    List<StudentOfficeDTO> findAllByStatus(StudentStatus status);
    @Query("SELECT new com.trident.egovernance.dto.StudentOfficeDTO(s.regdNo, s.studentName, s.course, s.branchCode, s.phNo, s.email, s.studentType, s.currentYear, p.parentContact) FROM STUDENT s LEFT JOIN s.personalDetails p WHERE s.status=:status")
    List<StudentOfficeDTO> findAllByStatusAlongWithParentContact(@Param("status") StudentStatus status);

    BasicStudentDto findByRegdNo(String regdNo);

    Long countAllByStatus(StudentStatus status);
    List<StudentOnlyDTO> findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(String admissionYear, Courses course, Integer currentYear, StudentType studentType);
    List<Student> findAllByCourseAndCurrentYear(Courses course, Integer currentYear);
    @Query("SELECT DISTINCT s FROM STUDENT s " +
    "LEFT JOIN FETCH STUDENT_ADMISSION_DETAILS " +
    "LEFT JOIN FETCH PERSONAL_DETAILS " +
    "LEFT JOIN FETCH STUDENT_CAREER " +
    "WHERE s.regdNo IN :regdNo")
    List<Student> findAllByRegdNo(@Param("regdNo") List<String> regdNos);

    @Query("SELECT DISTINCT s FROM STUDENT s " +
            "LEFT JOIN FETCH s.studentAdmissionDetails sad " +
            "LEFT JOIN FETCH s.personalDetails pd " +
            "LEFT JOIN FETCH s.studentCareer sc " +
            "LEFT JOIN FETCH s.hostel h " +
            "LEFT JOIN FETCH s.transport t " +
            "LEFT JOIN FETCH s.studentDocs sd " +
            "WHERE s.regdNo = :regdNo")
    Optional<Student> findByRegdNoComplete(String regdNo);

    @Query(value = """
SELECT DISTINCT
        b.COURSE AS course,
        b.BRANCHCODE AS branchCode,
        NVL(COUNT(s.REGDNO), 0) AS studentCount
    FROM
        BRANCH b 
    LEFT JOIN
        STUDENT s
    ON s.COURSE = b.COURSE AND s.BRANCH_CODE = b.BRANCHCODE AND s.STATUS=:status
    WHERE b.STUDENTTYPE=:studentType
    GROUP BY b.COURSE, b.BRANCHCODE
    ORDER BY b.COURSE, b.BRANCHCODE
""", nativeQuery = true)
    List<Object[]> findRawStudentCountsGroupedByCourseAndBranch(@Param("studentType") String studentType, @Param("status") String status);

//    @Query("SELECT s FROM STUDENT s WHERE s.batchId LIKE :batchId AND s.status=:status")
//    List<Student> findAllByBatchIdAndStatus(@Param("batchId") String batchId, @Param("status") StudentStatus status);
//
    List<Student> findAllByBatchIdLikeAndStatus(String batchId, StudentStatus status);

}
