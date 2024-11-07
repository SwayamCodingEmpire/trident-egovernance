package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.dto.StudentRequiredFieldsDTO;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.global.helpers.MrHead;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MasterTableServices {
    List<Fees> getFeesByBatchIdAndRegdYear(String batchId, Integer regdYear);
    List<StandardDeductionFormat> getStandardDeductionformatByDescriptions(Set<String> descriptions);
    public String getSessionId(String course, int regdYear, int admissionYear, String studentType);
    public Optional<StandardDeductionFormat> getStandardDeductionFormat(String description);
    List<FeeTypesMrHead> getFeeTypesMrHeadByDescriptions(List<String> descriptions);
    HashMap<String, MrHead> convertFeeTypesMrHeadToHashMap(List<String> descriptions);
    boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining);
    boolean isRelevantFee(Fees fee, StudentRequiredFieldsDTO student, Boolean plPool, Boolean indusTraining);

    FeeTypes getFeeTypesByFeeGroupAndSemester(String feeGroup, Integer semester);

}
