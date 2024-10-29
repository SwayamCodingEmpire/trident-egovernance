package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.OldDueDetails;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldDuesDetailsRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DuesDetailBackupServiceImpl {
    private final DuesDetailsRepository duesDetailsRepository;
    private final OldDuesDetailsRepository oldDuesDetailsRepository;
    private final Logger logger = LoggerFactory.getLogger(DuesDetailBackupServiceImpl.class);

    public DuesDetailBackupServiceImpl(DuesDetailsRepository duesDetailsRepository, OldDuesDetailsRepository oldDuesDetailsRepository) {
        this.duesDetailsRepository = duesDetailsRepository;
        this.oldDuesDetailsRepository = oldDuesDetailsRepository;
    }

    @Async
    public CompletableFuture<Boolean> transferToOldDuesDetails(List<String> regdNos, TransactionStatus status) {
        try{
            List<DuesDetails> duesDetails = duesDetailsRepository.findAllByRegdNoIn(regdNos);
            logger.info(duesDetails.toString());
            List<OldDueDetails> oldDueDetails = duesDetails.stream()
                    .map(OldDueDetails::new)
                    .toList();
            Pair<List<String>, HashMap<String, BigDecimal>> regdAndoldDues = createHashMapForRegdNoAndBalanceAmount(oldDueDetails);
            List<String> regdNosInMap = regdAndoldDues.getLeft();
            HashMap<String, BigDecimal> oldDuesCalculate = regdAndoldDues.getRight();
            removeFromHashMapWhichHasZeroBalaMT(regdNosInMap, oldDuesCalculate);

            List<DuesDetails> duesDetails1 = createThePreviousDueRecordInDuesDetailsEntities(oldDuesCalculate, oldDueDetails);

            List<OldDueDetails> oldDueDetails1 = oldDuesDetailsRepository.saveAll(oldDueDetails);
            logger.info(oldDueDetails1.toString());
            logger.info(duesDetails1.toString());
            duesDetailsRepository.deleteAllByRegdNoIn(regdNos);
            return CompletableFuture.completedFuture(true);
        }catch (Exception e){
            status.setRollbackOnly();
            return CompletableFuture.completedFuture(false);
        }
    }

    private String getNextSession(String currentSession) {
        String[] years = currentSession.split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);

        int nextStartYear = startYear + 1;
        int nextEndYear = endYear + 1;
        return nextStartYear + "-" + nextEndYear;
    }

    private Pair<List<String>,HashMap<String,BigDecimal>> createHashMapForRegdNoAndBalanceAmount(List<OldDueDetails> oldDueDetails){
        HashMap<String,BigDecimal> oldDuesCalculate = new HashMap<>();
        List<String> regdNosInMap = new ArrayList<>();
        for(OldDueDetails oldDueDetail : oldDueDetails) {
            if(oldDuesCalculate.containsKey(oldDueDetail.getRegdNo())) {
                oldDuesCalculate.put(oldDueDetail.getRegdNo(),oldDuesCalculate.get(oldDueDetail.getRegdNo()).add(oldDueDetail.getBalanceAmount()));
            }
            else if(oldDueDetail.getBalanceAmount().compareTo(BigDecimal.ZERO)!=0) {
                oldDuesCalculate.put(oldDueDetail.getRegdNo(),oldDueDetail.getBalanceAmount());
                regdNosInMap.add(oldDueDetail.getRegdNo());
            }
        }
        return Pair.of(regdNosInMap,oldDuesCalculate);
    }
    
    private void removeFromHashMapWhichHasZeroBalaMT(List<String> regdNosInMap,HashMap<String,BigDecimal> oldDuesCalculate){
        for(String regNo:regdNosInMap){
            if(oldDuesCalculate.get(regNo).equals(BigDecimal.ZERO)) {
                oldDuesCalculate.remove(regNo);
            }
        }
    }

    private List<DuesDetails> createThePreviousDueRecordInDuesDetailsEntities(HashMap<String, BigDecimal> oldDuesCalculate, List<OldDueDetails> oldDueDetails) {
        return oldDueDetails.parallelStream()
                .filter(oldDueDetail -> oldDuesCalculate.containsKey(oldDueDetail.getRegdNo()))
                .map(oldDueDetail -> {
                    DuesDetails duesDetails = new DuesDetails();
                    duesDetails.setRegdNo(oldDueDetail.getRegdNo());
                    BigDecimal balanceAmount = oldDuesCalculate.get(oldDueDetail.getRegdNo());
                    duesDetails.setBalanceAmount(balanceAmount);

                    if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                        duesDetails.setAmountDue(balanceAmount);
                        duesDetails.setAmountPaid(BigDecimal.ZERO);
                    } else {
                        duesDetails.setAmountDue(BigDecimal.ZERO);
                        duesDetails.setAmountPaid(balanceAmount);
                    }

                    duesDetails.setBalanceAmount(duesDetails.getAmountDue().subtract(duesDetails.getAmountPaid()));
                    duesDetails.setDescription("PREVIOUS DUE");
                    duesDetails.setDeductionOrder(1);
                    duesDetails.setSessionId(getNextSession(oldDueDetail.getSessionId()));
                    duesDetails.setAmountPaidToJee(oldDueDetail.getAmountPaidToJee());
                    duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
                    duesDetails.setDueYear(oldDueDetail.getDueYear());
                    duesDetails.setDiscount(oldDueDetail.getDiscount());
                    duesDetails.setAdjustment(oldDueDetail.getAdjustment());
                    return duesDetails;
                })
                .collect(Collectors.toList());
//        List<DuesDetails> duesDetailsList = new ArrayList<>();
//
//        for (OldDueDetails oldDueDetail : oldDueDetails) {
//            // Check if the oldDueDetail is not null and its registration number is in the oldDuesCalculate map
//            if (oldDueDetail != null && oldDuesCalculate.containsKey(oldDueDetail.getRegdNo())) {
//                DuesDetails duesDetails = new DuesDetails();
//                duesDetails.setRegdNo(oldDueDetail.getRegdNo());
//                BigDecimal balanceAmount = oldDuesCalculate.get(oldDueDetail.getRegdNo());
//                duesDetails.setBalanceAmount(balanceAmount);
//
//                if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
//                    duesDetails.setAmountDue(balanceAmount);
//                    duesDetails.setAmountPaid(BigDecimal.ZERO);
//                } else {
//                    duesDetails.setAmountDue(BigDecimal.ZERO);
//                    duesDetails.setAmountPaid(balanceAmount);
//                }
//
//                duesDetails.setBalanceAmount(duesDetails.getAmountDue().subtract(duesDetails.getAmountPaid()));
//                duesDetails.setDescription("PREVIOUS DUE");
//                duesDetails.setDeductionOrder(1);
//                duesDetails.setSessionId(getNextSession(oldDueDetail.getSessionId()));
//                duesDetails.setAmountPaidToJee(oldDueDetail.getAmountPaidToJee());
//                duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
//                duesDetails.setDueYear(oldDueDetail.getDueYear());
//                duesDetails.setDiscount(oldDueDetail.getDiscount());
//                duesDetails.setAdjustment(oldDueDetail.getAdjustment());
//
//                duesDetailsList.add(duesDetails);
//            }
//        }
//
//        return duesDetailsList;
    }

    public Boolean deleteFromDuesDetails(List<String> regdNos){
        duesDetailsRepository.deleteAllByRegdNoIn(regdNos);
        return true;
    }

    public List<DuesDetails> saveToDuesDetails(List<DuesDetails> duesDetails){
        return duesDetailsRepository.saveAll(duesDetails);
    }
}
