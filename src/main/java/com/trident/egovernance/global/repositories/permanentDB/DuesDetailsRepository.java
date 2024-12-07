package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.DuesSummaryDTO;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.dto.PaymentDuesDetails;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface DuesDetailsRepository extends JpaRepository<DuesDetails, DuesDetailsId> {
    List<DuesDetails> findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(String regdNo, BigDecimal balanceAmount);
    List<DuesDetails> findAllByRegdNoOrderByDeductionOrder(String regdNo);
    List<DuesDetails> findAllByRegdNoIn(Set<String> regdNos);
    @Modifying
    long deleteAllByRegdNoIn(Set<String> regdNos);

    @Query("SELECT SUM(d.amountDue) FROM DUESDETAIL d")
    BigDecimal sumAmountDueBySessionId();
    @Query("SELECT SUM(d.amountPaid) FROM DUESDETAIL d")
    BigDecimal sumAmountPaidBySessionId();
    @Query("SELECT SUM(d.balanceAmount) FROM DUESDETAIL d")
    BigDecimal sumBalanceAmountBySessionId();

    @Query("SELECT new com.trident.egovernance.dto.DuesSummaryDTO(SUM(d.amountDue),SUM(d.amountPaid),SUM(d.balanceAmount)) FROM DUESDETAIL d")
    DuesSummaryDTO getDuesDetailsSummary();

    @Query("SELECT new com.trident.egovernance.dto.PaymentDuesDetails(SUM(d.amountDue), SUM(d.amountPaid), SUM(d.balanceAmount)) FROM DUESDETAIL d WHERE d.regdNo = :regdNo AND d.description <> 'PREVIOUS DUE'")
    PaymentDuesDetails findSummaryByRegdNo(String regdNo);

    @Query("SELECT COALESCE(SUM(d.balanceAmount), 0) FROM DUESDETAIL d WHERE d.regdNo = :regdNo AND d.description = :arrears")
    BigDecimal getArrearsByRegdNo(String regdNo, String arrears);


    @Query("SELECT d FROM DUESDETAIL d WHERE CONCAT(d.regdNo, '::', d.description) IN :keys")
    List<DuesDetails> fetchDuesDetailsByRegdNoAndDescription(Set<String> keys);
}
