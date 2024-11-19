package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.DatabaseException;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldDuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.views.CollectionReportRepository;
import com.trident.egovernance.global.repositories.views.DailyCollectionSummaryRepository;
import com.trident.egovernance.global.services.DateConverterServices;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MapperServiceImpl;
import jakarta.persistence.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class AccountSectionServicesImpl implements AccountSectionService {
    private final DateConverterServices dateConverterServices;
    private final MapperService mapperService;
    private final OldDuesDetailsRepository oldDuesDetailsRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final FeeCollectionRepository feeCollectionRepository;
    private final StudentRepository studentRepository;
    private final MapperServiceImpl mapperServiceImpl;
    private final DailyCollectionSummaryRepository dailyCollectionSummaryRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountSectionServicesImpl.class);
    private final CollectionReportRepository collectionReportRepository;

    public AccountSectionServicesImpl(DateConverterServices dateConverterServices, MapperService mapperService, OldDuesDetailsRepository oldDuesDetailsRepository, DuesDetailsRepository duesDetailsRepository, FeeCollectionRepository feeCollectionRepository, StudentRepository studentRepository, MapperServiceImpl mapperServiceImpl, DailyCollectionSummaryRepository dailyCollectionSummaryRepository, CollectionReportRepository collectionReportRepository) {
        this.dateConverterServices = dateConverterServices;
        this.mapperService = mapperService;
        this.oldDuesDetailsRepository = oldDuesDetailsRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.feeCollectionRepository = feeCollectionRepository;
        this.studentRepository = studentRepository;
        this.mapperServiceImpl = mapperServiceImpl;
        this.dailyCollectionSummaryRepository = dailyCollectionSummaryRepository;
        this.collectionReportRepository = collectionReportRepository;
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
    public BasicStudentDto getBasicStudentDetails(String regdNo) {
        return studentRepository.findByRegdNo(regdNo);
    }

    @Override
    public FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo) {
        List<FeeCollection> feeCollectionsList = feeCollectionRepository.findAllByStudent_RegdNo(regdNo);
        Map<Integer, List<FeeCollection>> feeCollectionGroupedByYear = feeCollectionsList
                .stream()
                .collect(Collectors.groupingBy(FeeCollection::getDueYear));
        return new FeeCollectionHistoryDto(feeCollectionGroupedByYear.get(1), feeCollectionGroupedByYear.get(2), feeCollectionGroupedByYear.get(3), feeCollectionGroupedByYear.get(4));
    }

    public FeeDashboardSummary getDashBoardNumbers(String paymentDate) {
        return new FeeDashboardSummary(
                dailyCollectionSummaryRepository.sumCollectedFeesByDate(paymentDate),
                duesDetailsRepository.getDuesDetailsSummary());
    }

    public List<CollectionSummary> getFeeCollectionsBySessionId(String sessionId) {
        try {
            return feeCollectionRepository.findAllBySessionId(sessionId).stream()
                    .flatMap(feeCollection ->
                            feeCollection.getMrDetails().stream()
                                    .map(mrDetails -> new CollectionSummary(mrDetails))
                    )
                    .toList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

    public Set<CollectionSummary> getAllDailyCollectionSummaryByPaymentDate(String paymentDate) {
        return mapperService.convertToCollectionSummarySet(dailyCollectionSummaryRepository.findAllByPaymentDate(paymentDate));
    }

    public Set<DailyCollectionSummary> collectionSummaryByTimePeriod(String unit, int timePeriod) {
        Set<String> dates = new HashSet<>();
        switch (unit) {
            case "week":
                dates.addAll(dateConverterServices.getLastNumberOfDays(7 * timePeriod));
                break;
            case "month":
                dates.addAll(dateConverterServices.getLastNumberOfDays(30 * timePeriod));
                break;
            default:
                throw new DatabaseException("Unsupported unit");
        }
        return dailyCollectionSummaryRepository.findAllByPaymentDateIn(dates);
    }

    public List<CollectionReportDTO> getCollectionReportByDate(String paymentDate){
        List<Tuple> tuples = collectionReportRepository.findAllCollectionReportsWithMrDetailsByDate(paymentDate);
        return mapperService.convertFromTuplesToListOfCollectionReportDTO(tuples);
    }

    public List<CollectionReportDTO> getCollectionReportBetweenDates(Date startDate, Date endDate){
        List<Tuple> tuples = collectionReportRepository.findAllCollectionReportsWithMrDetailsByDateInBetween(startDate,endDate);
        return mapperService.convertFromTuplesToListOfCollectionReportDTO(tuples);
    }

}
