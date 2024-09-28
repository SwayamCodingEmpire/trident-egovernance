package com.trident.egovernance.services;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.entities.permanentDB.Fees;
import com.trident.egovernance.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.helpers.MrHead;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MasterTableServices {
    List<Fees> getFeesByBatchId(String batchId);
    List<StandardDeductionFormat> getStandardDeductionformatByDescriptions(Set<String> descriptions);
    public String getSessionId(String course, int regdYear, int admissionYear, String studentType);
    public Optional<StandardDeductionFormat> getStandardDeductionFormat(String description);
    List<FeeTypesMrHead> getFeeTypesMrHeadByDescriptions(List<String> descriptions);
    HashMap<String, MrHead> convertFeeTypesMrHeadToHashMap(List<String> descriptions);

}
