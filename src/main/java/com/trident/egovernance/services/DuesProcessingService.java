package com.trident.egovernance.services;

import com.trident.egovernance.entities.permanentDB.DuesDetails;
import com.trident.egovernance.entities.permanentDB.Fees;
import com.trident.egovernance.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.*;
import com.trident.egovernance.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.repositories.permanentDB.SessionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DuesProcessingService {
    private final DuesDetailsRepository duesDetailsRepository;
    private final SessionsRepository sessionsRepository;
    private final MasterTableServices masterTableServices;
    private final Logger logger = LoggerFactory.getLogger(DuesProcessingService.class);

    public DuesProcessingService(DuesDetailsRepository duesDetailsRepository, SessionsRepository sessionsRepository, MasterTableServices masterTableServices) {
        this.duesDetailsRepository = duesDetailsRepository;
        this.sessionsRepository = sessionsRepository;
        this.masterTableServices = masterTableServices;
    }


    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> initiateDuesDetails(NSR student, SharedStateAmongDueInitiationAndNSRService sharedState){
        try{
            logger.info("Fetching fees from database");
            List<Fees> fees = masterTableServices.getFeesByBatchId(student.getBatchId());
            isInterrupted(sharedState.isProceed());
            logger.info("Fetched fees from database");
            logger.info(fees.toString());
            Set<String> descriptions = fees
                    .stream()
                    .map(Fees::getDescription)
                    .collect(Collectors.toCollection(TreeSet::new));
            logger.info(fees.toString());
            Map<String, StandardDeductionFormat> deductionFormatMap = masterTableServices.getStandardDeductionformatByDescriptions(descriptions)
                    .stream()
                    .collect(Collectors.toMap(StandardDeductionFormat::getDescription, standardDeductionFormat -> standardDeductionFormat));
            logger.info(deductionFormatMap.toString());
            List<DuesDetails> duesDetailsList = new ArrayList<>();
            for (Fees fee : fees) {
                logger.info(fee.getTfwType().toString());
//                logger.info(fee.getFeeType().getType().toString());
                if (fee.getTfwType().equals(student.getTfw()) || fee.getTfwType().equals(TFWType.ALL)) {
                    if((FeeTypesType.fromDisplayName(fee.getFeeType().getType().getDisplayName()).equals(FeeTypesType.COMPULSORY_FEES))
                            || (student.getTransportOpted().equals(BooleanString.YES) && (fee.getFeeType().getFeeGroup().compareTo("TRANSPORTFEE") == 0))
                            || (student.getHostelOption().equals(BooleanString.YES) && (fee.getFeeType().getFeeGroup().compareTo("HOSTELFEE") == 0))){
                        DuesDetails duesDetails = new DuesDetails();
                        duesDetails.setRegdNo(student.getRegdNo());
                        duesDetails.setDescription(fee.getDescription());
                        duesDetails.setBalanceAmount(fee.getAmount());
                        duesDetails.setAmountPaid(BigDecimal.ZERO);
                        logger.info(duesDetails.getAmountPaid().toString());
                        logger.info(fee.getAmount().toString());
                        duesDetails.setAmountDue(fee.getAmount());
                        logger.info(duesDetails.getAmountDue().toString());
                        duesDetails.setDeductionOrder(deductionFormatMap.get(fee.getDescription()).getDeductionOrder());
                        duesDetails.setDueYear(student.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
                        duesDetails.setSessionId(masterTableServices.getSessionId(student.getCourse().getDisplayName(),duesDetails.getDueYear(),Year.now().getValue(), student.getStudentType().getEnumName()));
                        duesDetails.setAmountPaidToJee(BigDecimal.ZERO);
                        duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
                        duesDetailsList.add(duesDetails);
                    }
                }
            }
            logger.info("Saving dues details to database");
            isInterrupted(sharedState.isProceed());
            duesDetailsRepository.saveAllAndFlush(duesDetailsList);
            isInterrupted(sharedState.isProceed());
            logger.info("Saved dues details to database");
            return CompletableFuture.completedFuture(true);
        }catch (Exception e){
            logger.error("Error occurred while processing dues details : "+e.getMessage());
            throw new RuntimeException("Error occurred while processing dues details : "+e.getMessage());
        }
    }

    private void isInterrupted(boolean proceed) throws InterruptedException {
        if(!proceed){
            throw new InterruptedException();
        }
    }
}
