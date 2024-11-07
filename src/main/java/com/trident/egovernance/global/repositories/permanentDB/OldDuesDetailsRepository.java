package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.DuesDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.OldDueDetails;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OldDuesDetailsRepository extends JpaRepository<OldDueDetails, DuesDetailsId> {
    List<OldDueDetails> findAllByRegdNo(String regdNo);
    @Query("SELECT SUM(d.amountDue) FROM OLDDUEDDETAIL d WHERE d.sessionId = :sessionId")
    BigDecimal sumAmountDueBySessionId(String sessionId);
    @Query("SELECT SUM(d.amountPaid) FROM OLDDUEDDETAIL d WHERE d.sessionId = :sessionId")
    BigDecimal sumAmountPaidBySessionId(String sessionId);
    @Query("SELECT SUM(d.balanceAmount) FROM OLDDUEDDETAIL d WHERE d.sessionId = :sessionId")
    BigDecimal sumBalanceAmountBySessionId(String sessionId);
}
