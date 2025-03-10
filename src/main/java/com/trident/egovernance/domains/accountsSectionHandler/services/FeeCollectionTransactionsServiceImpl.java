package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import com.trident.egovernance.global.helpers.MrHead;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.PaymentDuesDetailsRepository;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MasterTableServices;
import com.trident.egovernance.global.services.MiscellaniousServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class FeeCollectionTransactionsServiceImpl implements FeeCollectionTransactionServices {
    private final Logger logger = LoggerFactory.getLogger(FeeCollectionTransactionsServiceImpl.class);
    private final ExecutorService executorService;
    private final FeeCollectionRepository feeCollectionRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final MasterTableServices masterTableServices;
    private final MapperService mapperService;
    private final PaymentDuesDetailsRepository paymentDuesDetailsRepository;
    private final MiscellaniousServices miscellaniousServices;

    public FeeCollectionTransactionsServiceImpl(FeeCollectionRepository feeCollectionRepository, DuesDetailsRepository duesDetailsRepository, MasterTableServices masterTableServices, MapperService mapperService, PaymentDuesDetailsRepository paymentDuesDetailsRepository, MiscellaniousServices miscellaniousServices) {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.feeCollectionRepository = feeCollectionRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.masterTableServices = masterTableServices;
        this.mapperService = mapperService;
        this.paymentDuesDetailsRepository = paymentDuesDetailsRepository;
        this.miscellaniousServices = miscellaniousServices;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public MoneyReceipt getMrDetailsSorted(FeeCollection processedFeeCollection, List<DuesDetails> processedDuesDetails) {
        System.out.println(processedFeeCollection.getMrDetails().toString());
        List<DuesDetails> savedDuesDetails = duesDetailsRepository.saveAllAndFlush(processedDuesDetails);
        BigDecimal currentDues = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal amountDue = BigDecimal.ZERO;
        BigDecimal arrears = BigDecimal.ZERO;
        for (DuesDetails d : savedDuesDetails) {
            if (d.getDescription().compareTo("PREVIOUS DUES") != 0) {
                currentDues = currentDues.add(d.getAmountDue());
                totalPaid = totalPaid.add(d.getAmountPaid());
                amountDue = amountDue.add(d.getBalanceAmount());
            } else {
                arrears = arrears.add(d.getBalanceAmount());
            }
        }
        PaymentDuesDetails paymentDuesDetails = new PaymentDuesDetails(arrears, currentDues, totalPaid, amountDue);
        com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails paymentDuesDetailsEntity = new com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails(paymentDuesDetails);
        paymentDuesDetailsEntity.setFeeCollection(processedFeeCollection);
        processedFeeCollection.setPaymentDuesDetails(paymentDuesDetailsEntity);
        logger.info("PaymentDuesDetailsEntity: " + paymentDuesDetailsEntity);
        FeeCollection savedFeeCollection = feeCollectionRepository.save(processedFeeCollection);
        savedFeeCollection.getPaymentMode();
        savedFeeCollection.getDdNo();
        savedFeeCollection.getDdDate();
        savedFeeCollection.getDdBank();

        return sortMrDetailsByMrHead(mapperService.convertToMrDetailsDtoSet(savedFeeCollection.getMrDetails()),
                new FeeCollectionDetails(savedFeeCollection),
                paymentDuesDetails
        );
    }

    @Override
    public MoneyReceipt sortMrDetailsByMrHead(List<MrDetailsDto> mrDetailsDtos, FeeCollectionDetails feeCollectionDetails, PaymentDuesDetails paymentDuesDetails) {
        List<String> descriptionsOfMrHead = mrDetailsDtos.stream()
                .map(MrDetailsDto::getParticulars)
                .collect(Collectors.toList());
        HashMap<String, MrHead> feeTypesMrHeadHashMap = masterTableServices.convertFeeTypesMrHeadToHashMap(descriptionsOfMrHead);
        List<MrDetailsDto> tat = new ArrayList<>();
        List<MrDetailsDto> tactF = new ArrayList<>();
        for (MrDetailsDto mrDetailsDto : mrDetailsDtos) {
            if (feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TAT)) {
                tat.add(mrDetailsDto);
            } else if (feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TACTF)) {
                tactF.add(mrDetailsDto);
            }
        }

        BigDecimal tatAmount = tat.stream()
                .map(MrDetailsDto::getAmount) // Extract amounts
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        MoneyDTO tatMoney = miscellaniousServices.convertMoneyToWords(tatAmount);
        BigDecimal tactFAmount = tactF.stream()
                .map(MrDetailsDto::getAmount) // Extract amounts
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        MoneyDTO tactMoney = miscellaniousServices.convertMoneyToWords(tactFAmount);
        tat.sort(Comparator.comparingLong(MrDetailsDto::getSlNo));
        tactF.sort(Comparator.comparingLong(MrDetailsDto::getSlNo));
        return new MoneyReceipt(
                feeCollectionDetails.date(),
                mrDetailsDtos.getFirst().getMrNo(),
                tat,
                tatMoney,
                tactF,
                tactMoney,
                feeCollectionDetails,
                paymentDuesDetails
        );
    }

//    @Override
//    public MoneyReceipt getMoneyReceiptByMrNo(Long mrNo) {
//        FeeCollection feeCollection = feeCollectionRepository.findByMrNo(mrNo)
//                .orElseThrow(() -> new RecordNotFoundException("No such fee collection found"));
//
//        com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails paymentDuesDetails = paymentDuesDetailsRepository.findById(mrNo).orElse(null);
//        return sortMrDetailsByMrHead(
//                mapperService.convertToMrDetailsDtoSet(feeCollection.getMrDetails()),
//                new FeeCollectionDetails(feeCollection),
//                new PaymentDuesDetails(paymentDuesDetails)
//        );
//    }

    @Override
    public MoneyReceipt getMoneyReceiptByMrNo(Long mrNo) {
        FeeCollection feeCollection = feeCollectionRepository.findByMrNo(mrNo)
                .orElseThrow(() -> new RecordNotFoundException("No such fee collection found"));

        // Async call to findSummaryByRegdNo
        CompletableFuture<PaymentDuesDetails> summaryFuture = CompletableFuture.supplyAsync(
                () -> duesDetailsRepository.findSummaryByRegdNo(feeCollection.getStudent().getRegdNo()), executorService
        );

        // Async call to getArrearsByRegdNo
        CompletableFuture<BigDecimal> arrearsFuture = CompletableFuture.supplyAsync(
                () -> duesDetailsRepository.getArrearsByRegdNo(feeCollection.getStudent().getRegdNo(), "PREVIOUS DUE"), executorService
        );

        // Combine the results of both futures
        CompletableFuture<MoneyReceipt> receiptFuture = CompletableFuture.allOf(summaryFuture, arrearsFuture)
                .thenApply(voidResult -> {
                            try {
                                // Both futures have completed successfully
                                PaymentDuesDetails summary = summaryFuture.join(); // Get result of summaryFuture
                                BigDecimal arrears = arrearsFuture.join();         // Get result of arrearsFuture

                                PaymentDuesDetails newSummary = new PaymentDuesDetails(arrears, summary.currentDues(), summary.totalPaid(), summary.amountDue());
                                // Create and populate the MoneyReceipt object
                                // Use provided logic to process data
                                return sortMrDetailsByMrHead(
                                        mapperService.convertToMrDetailsDtoSet(feeCollection.getMrDetails()),
                                        new FeeCollectionDetails(feeCollection),
                                        newSummary
                                );
                            } catch (Exception e) {
                                throw new RuntimeException("Error processing MoneyReceipt", e);
                            }
                        }
                );

        // Block and get the final result
        return receiptFuture.join();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteFeeCollectionRecord(FeeCollection oldFeeCollection) {
        logger.info(oldFeeCollection.getMrNo().toString());
        Set<String> uniqueKeys = oldFeeCollection.getMrDetails().stream()
                .map(mrDetails -> generateKey(oldFeeCollection.getStudent().getRegdNo(), mrDetails.getParticulars()))
                .collect(Collectors.toSet());
        List<DuesDetails> duesDetailsList = duesDetailsRepository.fetchDuesDetailsByRegdNoAndDescription(uniqueKeys);
        if(duesDetailsList.isEmpty() || duesDetailsList==null) {
            return feeCollectionRepository.deleteByMrNo(oldFeeCollection.getMrNo());
        }
        Map<String, DuesDetails> duesDetailsMap = duesDetailsList.stream()
                .collect(Collectors.toMap(
                        duesDetails -> generateKey(oldFeeCollection.getStudent().getRegdNo(), duesDetails.getDescription()),
                        duesDetails -> duesDetails
                ));
        for (MrDetails mrDetails : oldFeeCollection.getMrDetails()) {
            DuesDetails duesDetails = duesDetailsMap.get(generateKey(oldFeeCollection.getStudent().getRegdNo(), mrDetails.getParticulars()));
            if (duesDetails != null) {
                duesDetails.setAmountPaid(duesDetails.getAmountPaid().subtract(mrDetails.getAmount()));
                duesDetails.setBalanceAmount(duesDetails.getAmountDue().subtract(duesDetails.getAmountPaid().add(duesDetails.getAmountPaidToJee())));
            }
        }
        duesDetailsRepository.saveAll(duesDetailsList);
        return feeCollectionRepository.deleteByMrNo(oldFeeCollection.getMrNo());
    }

    private String generateKey(String regdNo, String description) {
        return regdNo + "::" + description;
    }
}
