package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.FeeCollectionDetails;
import com.trident.egovernance.dto.MrDetailsDto;
import com.trident.egovernance.dto.MoneyReceipt;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails;
import com.trident.egovernance.global.helpers.MrHead;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.services.MasterTableServices;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaveFeeCollection {
    private final FeeCollectionRepository feeCollectionRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final MasterTableServices masterTableServices;
    private final ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder;

    public SaveFeeCollection(FeeCollectionRepository feeCollectionRepository, DuesDetailsRepository duesDetailsRepository, MasterTableServices masterTableServices, ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder) {
        this.feeCollectionRepository = feeCollectionRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.masterTableServices = masterTableServices;
        this.threadPoolTaskExecutorBuilder = threadPoolTaskExecutorBuilder;
    }

    @Transactional
    public MoneyReceipt getMrDetailsSorted(FeeCollection processedFeeCollection, List<DuesDetails> processedDuesDetails) {
        System.out.println(processedFeeCollection.getMrDetails().toString());
        FeeCollection savedFeeCollection = feeCollectionRepository.save(processedFeeCollection);
        savedFeeCollection.getPaymentMode();
        savedFeeCollection.getDdNo();
        savedFeeCollection.getDdDate();
        savedFeeCollection.getDdBank();
        List<DuesDetails> savedDuesDetails = duesDetailsRepository.saveAllAndFlush(processedDuesDetails);
        BigDecimal currentDues = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal amountDue = BigDecimal.ZERO;
        BigDecimal arrears = BigDecimal.ZERO;
        for(DuesDetails d : savedDuesDetails) {
            if(d.getDescription().compareTo("PREVIOUS DUES") != 0) {
                currentDues = currentDues.add(d.getAmountDue());
                totalPaid = totalPaid.add(d.getAmountPaid());
                amountDue = amountDue.add(d.getBalanceAmount());
            }
            else{
                arrears = arrears.add(d.getBalanceAmount());
            }
        }
        return sortMrDetailsByMrHead(savedFeeCollection.getMrDetails().stream()
                .map(mrDetail ->
                        new MrDetailsDto(
                                savedFeeCollection.getMrNo(),
                                mrDetail.getId(),
                                mrDetail.getSlNo(),
                                mrDetail.getParticulars(),
                                mrDetail.getAmount()
                        )
                )
                .collect(Collectors.toList()),
                new FeeCollectionDetails(savedFeeCollection.getPaymentMode(),
                        savedFeeCollection.getDdNo(),
                        savedFeeCollection.getDdBank(),
                        savedFeeCollection.getDdDate()),
                new PaymentDuesDetails(arrears,currentDues,totalPaid,amountDue)
                );
    }

    public MoneyReceipt sortMrDetailsByMrHead(List<MrDetailsDto> mrDetailsDtos, FeeCollectionDetails feeCollectionDetails, PaymentDuesDetails paymentDuesDetails){
        List<String> descriptionsOfMrHead = mrDetailsDtos.stream()
                .map(MrDetailsDto::getParticulars)
                .collect(Collectors.toList());
        HashMap<String, MrHead> feeTypesMrHeadHashMap = masterTableServices.convertFeeTypesMrHeadToHashMap(descriptionsOfMrHead);
        List<MrDetailsDto> tat = new ArrayList<>();
        List<MrDetailsDto> tactF = new ArrayList<>();
        for(MrDetailsDto mrDetailsDto: mrDetailsDtos){
            if(feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TAT)){
                tat.add(mrDetailsDto);
            }
            else if(feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TACTF)){
                tactF.add(mrDetailsDto);
            }
        }
        return new MoneyReceipt(
                tat,
                tactF,
                feeCollectionDetails,
                paymentDuesDetails
        );
    }
}
