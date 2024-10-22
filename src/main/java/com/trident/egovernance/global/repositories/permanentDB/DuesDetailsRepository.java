package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.DuesDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DuesDetailsRepository extends JpaRepository<DuesDetails, DuesDetailsId> {
    List<DuesDetails> findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(String regdNo, BigDecimal balanceAmount);
    List<DuesDetailsDto> findAllByRegdNoOrderByDeductionOrder(String regdNo);
    List<DuesDetails> findAllByRegdNoIn(List<String> regdNos);
    long deleteAllByRegdNoIn(List<String> regdNos);
}
