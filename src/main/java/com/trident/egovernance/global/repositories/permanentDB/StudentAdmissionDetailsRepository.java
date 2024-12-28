package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.StudentAdmissionDetails;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.TFWType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Repository
public interface StudentAdmissionDetailsRepository extends JpaRepository<StudentAdmissionDetails,String> {
    @Modifying
    @Transactional
    @Query("UPDATE STUDENT_ADMISSION_DETAILS s SET "
            + "s.admissionDate = :admissionDate, "
            + "s.ojeeCounsellingFeePaid = :ojeeCounsellingFeePaid, "
            + "s.tfw = :tfw, "
            + "s.admissionType = :admissionType, "
            + "s.ojeeRollNo = :ojeeRollNo, "
            + "s.ojeeRank = :ojeeRank, "
            + "s.aieeeRank = :aieeeRank, "
            + "s.caste = :caste, "
            + "s.reportingDate = :reportingDate, "
            + "s.categoryCode = :categoryCode, "
            + "s.categoryRank = :categoryRank, "
            + "s.jeeApplicationNo = :jeeApplicationNo, "
            + "s.allotmentId = :allotmentId "
            + "WHERE s.regdNo = :regdNo")
    int updateStudentAdmissionDetails(
            Date admissionDate,
            BooleanString ojeeCounsellingFeePaid,
            TFWType tfw,
            String admissionType,
            String ojeeRollNo,
            String ojeeRank,
            String aieeeRank,
            String caste,
            Date reportingDate,
            String categoryCode,
            Long categoryRank,
            String jeeApplicationNo,
            String allotmentId,
            String regdNo
    );

    StudentAdmissionDetails findByRegdNo(String regdNo);
}
