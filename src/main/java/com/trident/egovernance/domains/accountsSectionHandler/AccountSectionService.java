package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.*;

import java.util.List;
import java.util.Map;

public interface AccountSectionService {
    Map<Integer, Map<Integer, List<DuesDetailsDto>>> getDuesDetails(String regdNo);
    BasicStudentDto getBasicStudentDetails(String regdNo);
    FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo);
    FeeDashboardSummary getDashBoardNumbers(String paymentDate);
    List<FeeCollectionOnlyDTO> getFeeCollectionBySessionId(String sessionId);
}
