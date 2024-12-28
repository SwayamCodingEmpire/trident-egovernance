package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.FeeTypesType;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountSectionService {
    Map<Integer, Map<Integer, List<DuesDetailsDto>>> getDuesDetails(String regdNo);
    List<CollectionReportDTO> getCollectionReportByDate(String paymentDate);
    List<CollectionReportDTO> getCollectionReportBetweenDates(Date startDate, Date endDate);
    BasicStudentDto getBasicStudentDetails(String regdNo);
    FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo, FeeTypesType feeTypesType);
    FeeDashboardSummary getDashBoardNumbers(String paymentDate);
    List<CollectionSummary> getFeeCollectionsBySessionId(String sessionId);
    Set<DailyCollectionSummary> collectionSummaryByTimePeriod(String unit, int timePeriod);
    Set<CollectionSummary> getAllDailyCollectionSummaryByPaymentDate(String paymentDate);

    List<FeeCollectionOnlyDTO> getFeeCollectionFilteredByPaymentDate(String paymentDate);
    List<FeeCollectionOnlyDTO> getFeeCollectionFilteredBySessionId(String sessionId);
    List<FeeTypesOnly> getFines();
    List<MrDetailsDTO> fetchMrDetailsByMrNo(Long mrNo);
//    FeeCollectionAndMrDetails getFeeCollectionBeMrNo(Long mrNo);
}
