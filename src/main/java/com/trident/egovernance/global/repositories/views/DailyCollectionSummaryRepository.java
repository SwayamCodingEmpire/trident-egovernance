package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.DailyCollectionSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Set;

@Repository
public interface DailyCollectionSummaryRepository extends JpaRepository<DailyCollectionSummary, DailyCollectionSummaryId> {
    Set<DailyCollectionSummary> findAllByPaymentDate(String paymentDate);
    Set<DailyCollectionSummary> findAllByPaymentDateIn(Set<String> paymentDates);
    @Query("SELECT SUM(d.totalAmount) FROM DailyCollectionSummary d WHERE d.paymentDate = :paymentDate")
    BigDecimal sumCollectedFeesByDate(String paymentDate);
}
