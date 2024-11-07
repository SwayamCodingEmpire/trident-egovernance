package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.StudentCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface StudentCareerRepository extends JpaRepository<StudentCareer, String> {
    @Modifying
    @Transactional
    @Query("UPDATE STUDENT_CAREER s SET s.tenthPercentage = :tenthPercentage, "
            + "s.tenthYOP = :tenthYOP, s.twelvthPercentage = :twelvthPercentage, "
            + "s.twelvthYOP = :twelvthYOP, s.diplomaPercentage = :diplomaPercentage, "
            + "s.diplomaYOP = :diplomaYOP, s.graduationPercentage = :graduationPercentage, "
            + "s.graduationYOP = :graduationYOP WHERE s.regdNo = :regdNo")
    int updateStudentCareer(
            BigDecimal tenthPercentage,
            Long tenthYOP,
            BigDecimal twelvthPercentage,
            Long twelvthYOP,
            BigDecimal diplomaPercentage,
            Long diplomaYOP,
            BigDecimal graduationPercentage,
            Long graduationYOP,
            String regdNo
    );
}
