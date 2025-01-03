package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Branch;
import com.trident.egovernance.global.entities.permanentDB.Course;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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

    @Query("""
    SELECT new com.trident.egovernance.dto.AdmissionData(
        s.course,
        s.branchCode,
        s.studentType,
        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'GENERAL' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'GENERAL' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'OBC' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'OBC' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'SC' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'SC' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND sa.caste = 'ST' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.caste = 'ST' THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND s.religion != :hindu THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND s.religion != :hindu THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND sa.tfw = :tfw THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.tfw = :tfw  THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male AND sa.tfw = :ntfw THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female AND sa.tfw = :ntfw  THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :male THEN 1 ELSE NULL END),
        COUNT(CASE WHEN s.gender = :female THEN 1 ELSE NULL END),
        COUNT(s)
    )
    FROM STUDENT s
    LEFT JOIN s.studentAdmissionDetails sa
    WHERE s.admissionYear = :admissionYear
    GROUP BY s.course, s.branchCode, s.studentType
""")
    List<AdmissionData> getStudentSummary(Gender male, Gender female, Religion hindu, TFWType tfw, TFWType ntfw, String admissionYear);


    @Query("""
    SELECT new com.trident.egovernance.dto.TotalAdmissionData(
        s.admissionYear,
        s.course,
        s.branchCode,
        s.studentType,
        COUNT(s)
    )
    FROM STUDENT s
    JOIN s.studentAdmissionDetails sa
    WHERE s.course = :course
      AND s.branchCode = :branch
    GROUP BY s.admissionYear, s.course, s.branchCode, s.studentType
""")
    List<TotalAdmissionData> getAdmissionSummaryByCourseAndBranch(Courses course, String branch);


    @Query("SELECT new com.trident.egovernance.dto.SessionWiseRecords(" +
            "se.sessionId, " +
            "se.course, " +
            "br.branch, " +
            "se.studentType, " +
            "se.regdYear, " +
            "COUNT(CASE WHEN s.gender = 'MALE' THEN 1 ELSE NULL END), " +
            "COUNT(CASE WHEN s.gender = 'FEMALE' THEN 1 ELSE NULL END), " +
            "COUNT(s)) " +
            "FROM STUDENT s " +
            "JOIN BRANCH br ON s.branchCode = br.branchCode AND s.course = br.course " +
            "JOIN SESSIONS se ON s.course = se.course AND CAST(s.admissionYear AS int) = CAST(se.admissionYear AS int) " +
            "AND s.studentType = se.studentType " +
            "AND s.currentYear = se.regdYear WHERE s.status = :status "+
            "GROUP BY se.sessionId, se.course, br.branch, se.studentType, se.regdYear")
    List<SessionWiseRecords> fetchSessionWiseStatistics(StudentStatus status);

    @Query("SELECT s.regdNo FROM STUDENT s WHERE s.regdNo = :regdNo")
    long findRegdNo(String regdNo);

    @Query("SELECT NEW com.trident.egovernance.dto.StudentBasicDTO(" +
            "s.regdNo, s.studentName, s.gender, s.branchCode, " +
            "s.admissionYear, s.currentYear, s.email) " +
            "FROM STUDENT s WHERE s.regdNo = :regdNo")
    StudentBasicDTO findBasicStudentData(String regdNo);

    // 2. Add enum fields one by one
    @Query("SELECT NEW com.trident.egovernance.dto.StudentWithEnumsDTO(" +
            "s.regdNo, s.studentName, s.gender, s.studentType, " +
            "s.hostelier, s.transportAvailed, s.status) " +
            "FROM STUDENT s WHERE s.regdNo = :regdNo")
    StudentWithEnumsDTO findStudentWithEnums(String regdNo);

    // 3. If both above work, try with joins
    @Query("SELECT s FROM STUDENT s " +
            "LEFT JOIN FETCH s.personalDetails " +
            "LEFT JOIN FETCH s.studentAdmissionDetails " +
            "LEFT JOIN FETCH s.studentCareer " +
            "WHERE s.regdNo = :regdNo")
    Student findStudentWithDetails(String regdNo);

    // 4. Finally, try with RollSheet and Section (since these worked before)
    @Query("SELECT s FROM STUDENT s " +
            "LEFT JOIN FETCH s.rollSheet " +
            "LEFT JOIN FETCH s.section " +
            "WHERE s.regdNo = :regdNo")
    Student findStudentWithRollAndSection(String regdNo);

    @Query("SELECT NEW com.trident.egovernance.dto.StudentDetailsDTO(" +
            "s.regdNo, " +
            "pd.parentContact, " +      // Add fields one by one from PersonalDetails
            "pd.parentEmailId, " +
            "pd.permanentAddress, " +
            "pd.permanentCity, " +
            "pd.permanentPincode, " +
            "pd.lgName " +     // From StudentAdmissionDetails
            ") " +
            "FROM STUDENT s " +
            "LEFT JOIN s.personalDetails pd " +
            "LEFT JOIN s.studentAdmissionDetails sad " +
            "WHERE s.regdNo = :regdNo")
    StudentDetailsDTO findStudentDetailsDiagnostic(String regdNo);

    @Query("SELECT new com.trident.egovernance.dto.DueStatusReport(" +
            "s.regdNo, s.currentYear, s.studentName, s.course, s.branchCode, " +
            "COALESCE((SELECT d2.amountDue FROM DUESDETAIL d2 WHERE d2.regdNo = s.regdNo AND d2.description = 'PREVIOUS DUE'), 0), " +  // arrearsDue with COALESCE
            "COALESCE(SUM(CASE WHEN d.description != 'PREVIOUS DUE' THEN d.amountDue ELSE 0 END), 0), " +  // currentDues
            "COALESCE(SUM(d.amountDue), 0), " +  // totalDues
            "COALESCE(SUM(CASE WHEN d.description = 'PREVIOUS DUE' THEN d.amountPaid ELSE 0 END), 0), " +  // arrearsPaid
            "COALESCE(SUM(CASE WHEN d.description != 'PREVIOUS DUE' THEN d.amountPaid ELSE 0 END), 0), " +  // currentDuesPaid
            "COALESCE(SUM(d.amountPaid), 0), " +  // totalPaid
            "COALESCE(SUM(d.amountDue), 0), " +  // amountDue
            "COALESCE(s.phNo, ''), " +  // phNo
            "COALESCE(p.parentContact, '') " +  // parentContact
            ") " +
            "FROM STUDENT s " +
            "LEFT JOIN DUESDETAIL d ON s.regdNo = d.regdNo " +
            "LEFT JOIN s.personalDetails p " +
            "WHERE s.course = :course AND s.branchCode = :branch AND s.currentYear = :dueYear " +
            "GROUP BY s.regdNo, s.currentYear, s.studentName, s.course, s.branchCode, s.phNo, p.parentContact")
    List<DueStatusReport> findAllByCourseAndBranchAndRegdYear(Courses course, String branch, Integer dueYear);



}

