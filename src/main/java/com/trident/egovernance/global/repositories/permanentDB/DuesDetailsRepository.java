package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Branch;
import com.trident.egovernance.global.entities.permanentDB.Course;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DuesDetailsRepository extends JpaRepository<DuesDetails, DuesDetailsId> {
    List<DuesDetails> findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(String regdNo, BigDecimal balanceAmount);
    List<DuesDetails> findAllByRegdNoOrderByDeductionOrder(String regdNo);
    List<DuesDetails> findAllByRegdNoIn(Set<String> regdNos);
    @Modifying
    @Query("DELETE FROM DUESDETAIL d WHERE d.regdNo IN :regdNos")
    long deleteAllByRegdNoIn(Set<String> regdNos);

    @Query("SELECT SUM(d.amountDue) FROM DUESDETAIL d")
    BigDecimal sumAmountDueBySessionId();
    @Query("SELECT SUM(d.amountPaid) FROM DUESDETAIL d")
    BigDecimal sumAmountPaidBySessionId();
    @Query("SELECT SUM(d.balanceAmount) FROM DUESDETAIL d")
    BigDecimal sumBalanceAmountBySessionId();

    @Query("SELECT new com.trident.egovernance.dto.DuesSummaryDTO(SUM(d.amountDue),SUM(d.amountPaid),SUM(d.balanceAmount)) FROM DUESDETAIL d")
    DuesSummaryDTO getDuesDetailsSummary();

    @Query("SELECT new com.trident.egovernance.dto.PaymentDuesDetails(SUM(d.amountDue), SUM(d.amountPaid), SUM(d.balanceAmount)) FROM DUESDETAIL d WHERE d.regdNo = :regdNo")
    PaymentDuesDetails findSummaryByRegdNo(String regdNo);

    @Query("SELECT COALESCE(SUM(d.balanceAmount), 0) FROM DUESDETAIL d WHERE d.regdNo = :regdNo AND d.description = :arrears")
    BigDecimal getArrearsByRegdNo(String regdNo, String arrears);


    @Query("SELECT d FROM DUESDETAIL d WHERE CONCAT(d.regdNo, '::', d.description) IN :keys")
    List<DuesDetails> fetchDuesDetailsByRegdNoAndDescription(Set<String> keys);


    @Query("SELECT new com.trident.egovernance.dto.DuesDetailsDto(d) FROM DUESDETAIL d WHERE d.regdNo = :regdNo")
    Set<DuesDetailsDto> findAllByRegdNo(String regdNo);

    @Query("SELECT new com.trident.egovernance.dto.ExcessFeeStudentData(" +
            "s.regdNo, "+
            "s.studentName, " +
            "s.branchCode, " +
            "s.admissionYear, " +
            "s.currentYear, " +
            "d.sessionId, " +
            "CAST(COALESCE(SUM(d.amountDue), 0) AS bigdecimal ), " +
            "CAST(COALESCE(SUM(d.amountPaid), 0) AS bigdecimal), " +
            "CAST(COALESCE(SUM(d.amountPaidToJee), 0) AS bigdecimal), " +
            "CAST(ABS(COALESCE(SUM(d.balanceAmount), 0)) AS bigdecimal)) " +
            "FROM DUESDETAIL d LEFT JOIN STUDENT s ON d.regdNo = s.regdNo " +
            "WHERE d.regdNo = :regdNo " +
            "GROUP BY s.studentName, s.branchCode, s.admissionYear, s.currentYear, d.sessionId, s.regdNo " +
            "HAVING SUM(d.balanceAmount) < 0")
    Optional<ExcessFeeStudentData> findStudentsWithExcessFee(@Param("regdNo") String regdNo);


    @Modifying
    @Query("UPDATE DUESDETAIL d SET d.amountDue = d.amountDue + :refundAmount, d.balanceAmount = d.balanceAmount + :refundAmount WHERE d.regdNo = :regdNo AND d.description = :description")
    void updateById(String regdNo, String description, BigDecimal refundAmount);
}
