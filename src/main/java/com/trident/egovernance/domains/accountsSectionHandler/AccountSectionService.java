package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.BasicStudentDto;
import com.trident.egovernance.dto.DuesDetailsSortedDto;
import com.trident.egovernance.dto.FeeCollectionHistoryDto;

public interface AccountSectionService {
    DuesDetailsSortedDto getDuesDetails(String regdNo);
    BasicStudentDto getBasicStudentDetails(String regdNo);
    FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo);
}
