package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
//    List<StudentOfficeDTO> findAllByStatus(StudentStatus status);
    @Query("SELECT new com.trident.egovernance.dto.StudentOfficeDTO(s.regdNo, s.studentName, s.course, s.branchCode, s.phNo, s.email, s.studentType, s.currentYear, p.parentContact) FROM STUDENT s LEFT JOIN s.personalDetails p WHERE s.status=:status")
    List<StudentOfficeDTO> findAllByStatusAlongWithParentContact(@Param("status") StudentStatus status);
    @Query("SELECT s FROM STUDENT s " +
            "LEFT JOIN FETCH s.personalDetails " +
            "LEFT JOIN FETCH s.section " +
            "LEFT JOIN FETCH s.rollSheet " +
            "WHERE s.regdNo = :regdNo")
    Student findStudentProfileData(String regdNo);

    BasicStudentDto findByRegdNo(String regdNo);
    @Query("SELECT s FROM STUDENT s LEFT JOIN FETCH STUDENT_ADMISSION_DETAILS LEFT JOIN FETCH HOSTEL LEFT JOIN FETCH TRANSPORT WHERE s.regdNo IN :regdNo")
    List<DuesDetailsInitiationDTO> findByRegdNoIn(Set<String> regdNo);

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
    LEFT JOIN 
            COURSE c
    ON b.COURSE = c.COURSE
    WHERE c.STUDENTTYPE=:studentType
    GROUP BY b.COURSE, b.BRANCHCODE
    ORDER BY b.COURSE, b.BRANCHCODE
""", nativeQuery = true)
    List<Object[]> findRawStudentCountsGroupedByCourseAndBranch(@Param("studentType") String studentType, @Param("status") String status);

//    @Query("SELECT s FROM STUDENT s WHERE s.batchId LIKE :batchId AND s.status=:status")
//    List<Student> findAllByBatchIdAndStatus(@Param("batchId") String batchId, @Param("status") StudentStatus status);
//
    List<Student> findAllByBatchIdLikeAndStatus(String batchId, StudentStatus status);

    @Modifying
    @Query("UPDATE STUDENT s SET s.studentName = :studentName, s.gender = :gender, s.dob = :dob, s.course = :course, " +
            "s.branchCode = :branchCode, s.admissionYear = :admissionYear, s.degreeYop = :degreeYop, s.phNo = :phNo, " +
            "s.email = :email, s.studentType = :studentType, s.hostelier = :hostelier, s.transportAvailed = :transportAvailed, " +
            "s.status = :status, s.batchId = :batchId, s.currentYear = :currentYear, s.aadhaarNo = :aadhaarNo, " +
            "s.indortrng = :indortrng, s.plpoolm = :plpoolm, s.cfPayMode = :cfPayMode, s.religion = :religion WHERE s.regdNo = :oldRegdNo")
    int updateStudent(
            String studentName,
            Gender gender,
            String dob,
            Courses course,
            String branchCode,
            String admissionYear,
            Integer degreeYop,
            String phNo,
            String email,
            StudentType studentType,
            BooleanString hostelier,
            BooleanString transportAvailed,
            StudentStatus status,
            String batchId,
            Integer currentYear,
            Long aadhaarNo,
            BooleanString indortrng,
            BooleanString plpoolm,
            CfPaymentMode cfPayMode,
            Religion religion,
//            String section,
            String oldRegdNo
    ) throws DataIntegrityViolationException, ConstraintViolationException, SQLException;

    @Query("UPDATE STUDENT s SET s.currentYear = s.currentYear+1 WHERE s.regdNo IN :regdNos")
    int updateStudentCurrentYearByRegdNo(Set<String> regdNo);

    @Query("SELECT new com.trident.egovernance.dto.DuesDetailsInitiationDTO(s.regdNo, s.studentType, s.indortrng, s.plpoolm, s.studentAdmissionDetails.tfw, s.transport.transportOpted, s.hostel.hostelOption, s.hostel.hostelChoice, s.currentYear, s.course, s.batchId) FROM STUDENT s LEFT JOIN s.studentCareer LEFT JOIN s.studentAdmissionDetails LEFT JOIN s.transport LEFT JOIN s.hostel WHERE s.regdNo IN :regdNo")
    List<DuesDetailsInitiationDTO> findStudentByRegdNo(Set<String> regdNo);


    @Query("SELECT COALESCE(COUNT(s.regdNo),0.00) FROM STUDENT s WHERE s.gender = :gender")
    long countStudentByGender(Gender gender);
}
