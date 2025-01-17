package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.DatabaseException;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.entities.views.FeeCollectionView;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import com.trident.egovernance.global.helpers.ExcessRefundID;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.repositories.permanentDB.*;
import com.trident.egovernance.global.repositories.views.CollectionReportRepository;
import com.trident.egovernance.global.repositories.views.DailyCollectionSummaryRepository;
import com.trident.egovernance.global.repositories.views.FeeCollectionViewRepository;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MiscellaniousServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountSectionServicesImpl implements AccountSectionService {
    private final MiscellaniousServices miscellaniousServices;
    private final MapperService mapperService;
    private final OldDuesDetailsRepository oldDuesDetailsRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final FeeCollectionRepository feeCollectionRepository;
    private final StudentRepository studentRepository;
    private final DailyCollectionSummaryRepository dailyCollectionSummaryRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountSectionServicesImpl.class);
    private final CollectionReportRepository collectionReportRepository;
    private final FeeTypesRepository feeTypesRepository;
    private final MrDetailsRepository mrDetailsRepository;
    private final FeeCollectionViewRepository feeCollectionViewRepository;
    private final AlterFeeCollectionRepository alterFeeCollectionRepository;
    private final ExcessRefundRepository excessRefundRepository;
    private final StandardDeductionFormatRepository standardDeductionFormatRepository;
    private final FeesRepository feesRepository;

    public AccountSectionServicesImpl(MiscellaniousServices miscellaniousServices, MapperService mapperService, OldDuesDetailsRepository oldDuesDetailsRepository, DuesDetailsRepository duesDetailsRepository, FeeCollectionRepository feeCollectionRepository, StudentRepository studentRepository, DailyCollectionSummaryRepository dailyCollectionSummaryRepository, CollectionReportRepository collectionReportRepository, FeeTypesRepository feeTypesRepository, MrDetailsRepository mrDetailsRepository, FeeCollectionViewRepository feeCollectionViewRepository, AlterFeeCollectionRepository alterFeeCollectionRepository, ExcessRefundRepository excessRefundRepository, StandardDeductionFormatRepository standardDeductionFormatRepository, FeesRepository feesRepository) {
        this.miscellaniousServices = miscellaniousServices;
        this.mapperService = mapperService;
        this.oldDuesDetailsRepository = oldDuesDetailsRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.feeCollectionRepository = feeCollectionRepository;
        this.studentRepository = studentRepository;
        this.dailyCollectionSummaryRepository = dailyCollectionSummaryRepository;
        this.collectionReportRepository = collectionReportRepository;
        this.feeTypesRepository = feeTypesRepository;
        this.mrDetailsRepository = mrDetailsRepository;
        this.feeCollectionViewRepository = feeCollectionViewRepository;
        this.alterFeeCollectionRepository = alterFeeCollectionRepository;
        this.excessRefundRepository = excessRefundRepository;
        this.standardDeductionFormatRepository = standardDeductionFormatRepository;
        this.feesRepository = feesRepository;
    }


    @Override
    public Map<Integer, Map<Integer, List<DuesDetailsDto>>> getDuesDetails(String regdNo) {
        // Stream directly from repository calls
        return Stream.concat(
                        mapperService.convertToDuesDetailsDto(duesDetailsRepository.findAllByRegdNoOrderByDeductionOrder(regdNo)).stream(),
                        mapperService.convertToDuesDetailsDtoFromOldDuesDetails(oldDuesDetailsRepository.findAllByRegdNo(regdNo)).stream())
                .collect(Collectors.groupingBy(
                        DuesDetailsDto::dueYear,
                        Collectors.groupingBy(DuesDetailsDto::sem)));
//        return new DuesDetailsSortedDto(duesGroupedByYear.get(1),duesGroupedByYear.get(2),duesGroupedByYear.get(3),duesGroupedByYear.get(4));
    }

    @Override
    public StudentBasicDTO getBasicStudentDetails(String regdNo) {
        return studentRepository.findBasicStudentData(regdNo);
    }

    @Override
    public FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo, FeeTypesType feeTypes) {
        List<FeeCollection> feeCollectionsList = feeCollectionRepository.findAllByStudent_RegdNo(regdNo);
        // Update the field and extract the amount from the first MrDetails
            List<FeeCollection> updatedFeeCollections = feeCollectionsList.stream()
                    .filter(feeCollection ->
                            feeCollection.getMrDetails().stream()
                                    .findFirst()
                                    .map(mrDetails -> checkFeeTypes(mrDetails, feeTypes))
                                    .orElse(false) // Default to true if no MrDetails exist
                    )
                    .peek(feeCollection -> {
                        // Extract value from Set<MrDetails>
                        feeCollection.getMrDetails().stream()
                                .findFirst()
                                .ifPresent(mrDetails -> {
                                    // Assuming you want to set some value from MrDetails to FeeCollection
                                    feeCollection.setType(mrDetails.getFeeType().getType().equals(FeeTypesType.OTHER_FEES)
                                            ? FeeTypesType.OTHER_FEES.getDisplayName()
                                            : "FEES");
                                });
                    })
                    .toList();


        // Grouping the updated list by due year
        Map<Integer, List<FeeCollectionAndMrDetails>> feeCollectionGroupedByYear = updatedFeeCollections.stream()
                .map(feeCollection -> new FeeCollectionAndMrDetails(new FeeCollectionOnlyDTO(feeCollection), mapperService.convertToMrDetailsDTOSet(feeCollection.getMrDetails())))
                .collect(Collectors.groupingBy(feeCollectionAndMrDetails -> feeCollectionAndMrDetails.feeCollection().dueYear()));

        return new FeeCollectionHistoryDto(
                feeCollectionGroupedByYear.get(1),
                feeCollectionGroupedByYear.get(2),
                feeCollectionGroupedByYear.get(3),
                feeCollectionGroupedByYear.get(4)
        );
    }

    private boolean checkFeeTypes(MrDetails mrDetails, FeeTypesType feeTypesType) {
        switch (feeTypesType) {
            case OTHER_FEES:
                return mrDetails.getFeeType().getType().equals(FeeTypesType.OTHER_FEES);
            default:
                return !mrDetails.getFeeType().getType().equals(FeeTypesType.OTHER_FEES);
        }
    }

//    public FeeCollectionByMrNo getFeeCollectionBeMrNo(Long mrNo){
//        FeeCollection feeCollection = feeCollectionRepository.findByMrNo(mrNo);
//        return FeeCollectionByMrNo(new FeeCollectionOnlyDTO(feeCollection),)
//    }

    public FeeDashboardSummary getDashBoardNumbers(String paymentDate) {
        return new FeeDashboardSummary(
                dailyCollectionSummaryRepository.sumCollectedFeesByDate(paymentDate),
                duesDetailsRepository.getDuesDetailsSummary());
    }

    public List<CollectionSummary> getFeeCollectionsBySessionId(String sessionId) {
        try {
            logger.info(sessionId);
            List<CollectionSummary> collectionSummaries =  feeCollectionRepository.findAllBySessionId(sessionId).stream()
                    .flatMap(feeCollection ->
                            feeCollection.getMrDetails().stream()
                                    .map(mrDetails -> new CollectionSummary(mrDetails))
                    )
                    .toList();
            logger.info(collectionSummaries.toString());
            return collectionSummaries;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

    public Set<CollectionSummary> getAllDailyCollectionSummaryByPaymentDate(String paymentDate) {
        logger.info(paymentDate);
        Set<DailyCollectionSummary> dailyCollectionSummaries =  dailyCollectionSummaryRepository.findAllByPaymentDate(paymentDate);
        logger.info(dailyCollectionSummaries.toString());
        Set<CollectionSummary> collectionSummaries =  mapperService.convertToCollectionSummarySet(dailyCollectionSummaries);
        return collectionSummaries;
    }



    public Set<DailyCollectionSummary> collectionSummaryByTimePeriod(String unit, int timePeriod) {
        Set<String> dates = new HashSet<>();
        switch (unit) {
            case "week":
                dates.addAll(miscellaniousServices.getLastNumberOfDays(7 * timePeriod));
                break;
            case "month":
                dates.addAll(miscellaniousServices.getLastNumberOfDays(30 * timePeriod));
                break;
            default:
                throw new DatabaseException("Unsupported unit");
        }
        return dailyCollectionSummaryRepository.findAllByPaymentDateIn(dates);
    }

    public CollectionReportDTO getCollectionReportByDate(String paymentDate) {
//        List<Tuple> tuples = collectionReportRepository.findAllCollectionReportsWithMrDetailsByDate(paymentDate);
//        return mapperService.convertFromTuplesToListOfCollectionReportDTO(tuples);
        List<FeeCollectionView> feeCollectionViews = feeCollectionViewRepository.findAllCollectionReportsWithMrDetailsByDate(Date.valueOf(paymentDate));
        Map<String, BigDecimal> descSum = new HashMap<>();
        for(FeeCollectionView feeCollectionView : feeCollectionViews) {
            feeCollectionView.getMrDetails().stream()
                    .forEach(mrDetails -> {
                        if(descSum.containsKey(mrDetails.getParticulars())) {
                            descSum.get(mrDetails.getParticulars()).add(mrDetails.getAmount());
                        }
                        else {
                            descSum.put(mrDetails.getParticulars(), mrDetails.getAmount());
                        }
                    });
            feeCollectionView.setMrDetailsDTOSet(mapperService.convertToMrDetailsDTOSet(feeCollectionView.getMrDetails()));
        }
        return new CollectionReportDTO(feeCollectionViews,descSum);
    }

    public CollectionReportDTO getCollectionReportBetweenDates(Date startDate, Date endDate) {
//        List<Tuple> tuples = collectionReportRepository.findAllCollectionReportsWithMrDetailsByDateInBetween(startDate, endDate);
//        return mapperService.convertFromTuplesToListOfCollectionReportDTO(tuples);
        List<FeeCollectionView> feeCollectionViews = feeCollectionViewRepository.findAllCollectionReportsWithMrDetailsBetweenDate(startDate,endDate);
        logger.info(feeCollectionViews.toString());
        Map<String, BigDecimal> descSum = new HashMap<>();
        for (FeeCollectionView feeCollectionView : feeCollectionViews) {
            feeCollectionView.getMrDetails().forEach(mrDetails -> {
                descSum.put(mrDetails.getParticulars(),
                        descSum.getOrDefault(mrDetails.getParticulars(), BigDecimal.ZERO)
                                .add(mrDetails.getAmount()));
            });
            feeCollectionView.setMrDetailsDTOSet(mapperService.convertToMrDetailsDTOSet(feeCollectionView.getMrDetails()));
        }
        logger.info(feeCollectionViews.toString());
        logger.info(descSum.toString());
        logger.info(new CollectionReportDTO(feeCollectionViews,descSum).toString());
        return new CollectionReportDTO(feeCollectionViews,descSum);
    }

    public CollectionReportDTO getCollectionReportBySessionId(String sessionId) {
//        List<Tuple> tuples = collectionReportRepository.findAllCollectionReportsWithMrDetailsByDateInBetween(startDate, endDate);
//        return mapperService.convertFromTuplesToListOfCollectionReportDTO(tuples);
        List<FeeCollectionView> feeCollectionViews = feeCollectionViewRepository.findFeeCollectionViewBySessionId(sessionId);
        logger.info(feeCollectionViews.toString());
        Map<String, BigDecimal> descSum = new HashMap<>();
        for(FeeCollectionView feeCollectionView : feeCollectionViews) {
            feeCollectionView.getMrDetails().stream()
                    .forEach(mrDetails -> {
                        if(descSum.containsKey(mrDetails.getParticulars())) {
                            descSum.get(mrDetails.getParticulars()).add(mrDetails.getAmount());
                        }
                        else {
                            descSum.put(mrDetails.getParticulars(), mrDetails.getAmount());
                        }
                    });
            feeCollectionView.setMrDetailsDTOSet(mapperService.convertToMrDetailsDTOSet(feeCollectionView.getMrDetails()));
        }
        return new CollectionReportDTO(feeCollectionViews,descSum);
    }

    public List<FeeCollectionOnlyDTO> getFeeCollectionFilteredByPaymentDate(String paymentDate) {
        return mapperService.convertToFeeCollectionOnlyDTOList(feeCollectionRepository.findAllByPaymentDate(paymentDate));
    }

    public List<FeeCollectionOnlyDTO> getFeeCollectionFilteredBySessionId(String sessionId) {
        return mapperService.convertToFeeCollectionOnlyDTOList(feeCollectionRepository.findAllBySessionId(sessionId));
    }

    public List<MrDetailsDTO> fetchMrDetailsByMrNo(Long mrNo) {
        return mrDetailsRepository.findAllByMrNo(mrNo);
    }

    @Override
    public List<DueStatusReport> fetchDueStatusReport(Optional<Courses> course, Optional<String> branch, Optional<Integer> regdYear) {
        return studentRepository.findAllByCourseAndBranchAndRegdYear(course.orElse(null), branch.orElse(null), regdYear.orElse(null));
    }

    @Override
    public Boolean addToAlterQueue(AlterFeeCollection feeCollection) {
        try{
            alterFeeCollectionRepository.save(feeCollection);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public ExcessFeeStudentData findStudentsWithExcessFee(String regdNo) {
        logger.info("findStudentsWithExcessFee");
        if(!studentRepository.existsById(regdNo)){
            throw new RecordNotFoundException("Invalid Registration No");
        }
        return duesDetailsRepository.findStudentsWithExcessFee(regdNo).orElseThrow(()->new RecordNotFoundException("Student has no pending Excess Fees"));
    }

    @Transactional
    public void insertRefundData(ExcessRefund excessRefund){
//        String paymentReceiver = miscellaniousServices.getUserJobInformation().name();
        String paymentReceiver = "DemoReciever";
        ExcessFeeStudentData excessFeeStudentData = duesDetailsRepository.findStudentsWithExcessFee(excessRefund.getRegdNo()).orElseThrow(()->new RecordNotFoundException("Student has no pending Excess Fees"));
        if(excessRefundRepository.existsById(new ExcessRefundID(excessRefund.getRegdNo(), excessRefund.getVoucherNo()))){
            throw new InvalidInputsException("Current Voucher number already used with this regdNo.");
        }
        excessRefund.setVoucherDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        excessRefund.setExcessFeePaid(excessFeeStudentData.excessFeePaid());
        excessRefund.setRegdYear(String.valueOf(excessFeeStudentData.regdyear()));
        excessRefund.setSessionId(excessFeeStudentData.sessionId());
        excessRefund.setPayer(paymentReceiver);
        excessRefundRepository.save(excessRefund);
        StandardDeductionFormat standardDeductionFormat = standardDeductionFormatRepository.findById("EXCESS FEE REFUND").orElse(null);
        assert standardDeductionFormat != null;
        if(!duesDetailsRepository.existsById(new DuesDetailsId(excessFeeStudentData.regdNo(), standardDeductionFormat.getDescription()))){
            DuesDetails details = new DuesDetails(excessRefund, excessFeeStudentData, standardDeductionFormat);
            duesDetailsRepository.save(details);
        }
        else{
            duesDetailsRepository.updateById(excessFeeStudentData.regdNo(), standardDeductionFormat.getDescription(), excessRefund.getRefundAmount());
        }
    }



    @Override
    @Transactional
    public void insertFees(FeesCRUDDto feesCRUDDto) {
        Long feeId = feesRepository.getMaxIdForFees();
        String batchId = miscellaniousServices.generateBatchId(feesCRUDDto.batchElements());
        for(Fees fees : feesCRUDDto.feesList()){
            fees.setBatchId(batchId);
            fees.setFeeId(feeId);
            feeId++;
        }
        feesRepository.saveAllAndFlush(feesCRUDDto.feesList());
    }
}
