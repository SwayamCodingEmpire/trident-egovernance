package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.AlterFeeCollection;
import com.trident.egovernance.global.entities.permanentDB.ExcessRefund;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.FeeTypesType;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AccountSectionService {
    Map<Integer, Map<Integer, List<DuesDetailsDto>>> getDuesDetails(String regdNo);
    CollectionReportDTO getCollectionReportByDate(String paymentDate);
    CollectionReportDTO getCollectionReportBetweenDates(Date startDate, Date endDate);
    CollectionReportDTO getCollectionReportBySessionId(String sessionId);
    StudentBasicDTO getBasicStudentDetails(String regdNo) ;
    FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo, FeeTypesType feeTypesType);
    FeeDashboardSummary getDashBoardNumbers(String paymentDate);
    List<CollectionSummary> getFeeCollectionsBySessionId(String sessionId);
    Set<DailyCollectionSummary> collectionSummaryByTimePeriod(String unit, int timePeriod);
    Set<CollectionSummary> getAllDailyCollectionSummaryByPaymentDate(String paymentDate);

    List<FeeCollectionOnlyDTO> getFeeCollectionFilteredByPaymentDate(String paymentDate);
    List<FeeCollectionOnlyDTO> getFeeCollectionFilteredBySessionId(String sessionId);
    List<MrDetailsDTO> fetchMrDetailsByMrNo(Long mrNo);

    List<DueStatusReport> fetchDueStatusReport(Optional<Courses> course, Optional<String> branch, Optional<Integer> regdYear);

    Boolean addToAlterQueue(AlterFeeCollection feeCollection);


    ExcessFeeStudentData findStudentsWithExcessFee(String regdNo);
//    FeeCollectionAndMrDetails getFeeCollectionBeMrNo(Long mrNo);
    void insertRefundData(ExcessRefund excessRefund);

    void insertFees(FeesCRUDDto feesCRUDDto);
}
