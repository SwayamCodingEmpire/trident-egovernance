package com.trident.egovernance.services;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.entities.permanentDB.Fees;
import com.trident.egovernance.entities.permanentDB.Sessions;
import com.trident.egovernance.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.MrHead;
import com.trident.egovernance.helpers.SessionIdId;
import com.trident.egovernance.repositories.permanentDB.FeeTypesRepository;
import com.trident.egovernance.repositories.permanentDB.FeesRepository;
import com.trident.egovernance.repositories.permanentDB.SessionsRepository;
import com.trident.egovernance.repositories.permanentDB.StandardDeductionFormatRepository;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MasterTableServices {
    private final Logger logger = LoggerFactory.getLogger(MasterTableServices.class);
    private final FeesRepository feesRepository;
    private final SessionsRepository sessionsRepository;
    private final FeeTypesRepository feeTypesRepository;
    private final StandardDeductionFormatRepository standardDeductionFormatRepository;

    public MasterTableServices(FeesRepository feesRepository, SessionsRepository sessionsRepository, FeeTypesRepository feeTypesRepository, StandardDeductionFormatRepository standardDeductionFormatRepository) {
        this.feesRepository = feesRepository;
        this.sessionsRepository = sessionsRepository;
        this.feeTypesRepository = feeTypesRepository;
        this.standardDeductionFormatRepository = standardDeductionFormatRepository;
    }
    @Cacheable(value = "fees", key = "#batchId")
    public List<Fees> getFeesByBatchId(String batchId){
        logger.info("Fetching fees by batchId: {}", batchId);
        try {
            List<Fees> fees = feesRepository.findAllByBatchId(batchId);
            logger.info("Fetched fees by batchId: {}", fees);
            return fees;
        } catch (Exception e) {
            logger.error("Error while fetching fees by batchId", e);
            return Collections.emptyList(); // Or handle appropriately
        }
    }


    @Cacheable(value = "standardDeductionFormat", key = "#descriptions.toString()")
    public List<StandardDeductionFormat> getStandardDeductionformatByDescriptions(Set<String> descriptions){
        logger.info("Fetching standardDeductionFormat by descriptions: {}", descriptions);
        return standardDeductionFormatRepository.findByDescriptions(descriptions);
    }

    @Cacheable(value = "SessionId", key = "#course+#regdYear+#admissionYear+#studentType")
    public String getSessionId(String course, int regdYear, int admissionYear, String studentType){
//        String currYear = String.valueOf(Year.now().getValue());
//        String nextYear = String.valueOf(Year.now().getValue()+1);
//        return currYear+"-"+nextYear;
        SessionIdId sessionIdId = new SessionIdId(course,regdYear, admissionYear,studentType);
        logger.info(sessionIdId.toString());
        Sessions sessions = sessionsRepository.findById(sessionIdId).orElseThrow(() -> new RecordNotFoundException("Session not found"));
        logger.info(sessions.toString());
        logger.info(sessions.toString());
        return sessions.getSessionId();
    }

    @Cacheable(value = "singularStandardDeductionFormat", key = "#description")
    public Optional<StandardDeductionFormat> getStandardDeductionFormat(String description) {
        return standardDeductionFormatRepository.findById(description);
    }

    public List<FeeTypesMrHead> getFeeTypesMrHeadByDescriptions(List<String> descriptions){
        return feeTypesRepository.findByDescriptionIn(descriptions);
    }

    public HashMap<String, MrHead> convertFeeTypesMrHeadToHashMap(List<String> descriptions){
        List<FeeTypesMrHead> feeTypesMrHeads = feeTypesRepository.findByDescriptionIn(descriptions);
        HashMap<String, MrHead> feeTypesMrHeadHashMap = new HashMap<>();
        for (FeeTypesMrHead feeTypesMrHead : feeTypesMrHeads) {
            feeTypesMrHeadHashMap.put(feeTypesMrHead.description(), feeTypesMrHead.mrHead());
        }
        return feeTypesMrHeadHashMap;
    }
}
