package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountSectionService {
    Map<Integer, Map<Integer, List<DuesDetailsDto>>> getDuesDetails(String regdNo);
    BasicStudentDto getBasicStudentDetails(String regdNo);
    FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo);
    FeeDashboardSummary getDashBoardNumbers(String paymentDate);
    List<CollectionSummary> getFeeCollectionsBySessionId(String sessionId);
    Set<DailyCollectionSummary> collectionSummaryByTimePeriod(String unit, int timePeriod);
    Set<CollectionSummary> getAllDailyCollectionSummaryByPaymentDate(String paymentDate);
}
