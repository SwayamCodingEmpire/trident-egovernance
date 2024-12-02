package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.domains.accountsSectionHandler.services.FeeCollectionTransactions;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.PaymentMode;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServicesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts-section")
public class AccountSectionController {
    private final FeeCollectionTransactions feeCollectionTransactions;
    private final AccountSectionService accountSectionService;
    private final MiscellaniousServicesImpl miscellaniousServices;

    private final FeeCollectionRepository feeCollectionRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountSectionController.class);
    private final MasterTableServicesImpl masterTableServicesImpl;
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public AccountSectionController(FeeCollectionTransactions feeCollectionTransactions, AccountSectionService accountSectionService, MiscellaniousServicesImpl miscellaniousServices, FeeCollectionRepository feeCollectionRepository, MasterTableServicesImpl masterTableServicesImpl) {
        this.feeCollectionTransactions = feeCollectionTransactions;
        this.accountSectionService = accountSectionService;
        this.miscellaniousServices = miscellaniousServices;
        this.feeCollectionRepository = feeCollectionRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
    }

    @GetMapping("/getDuesDetails/{regdNo}")
    public ResponseEntity<Map<Integer, Map<Integer, List<DuesDetailsDto>>>> getDuesDetails(@PathVariable("regdNo") String regdNo) {
        return ResponseEntity.ok(accountSectionService.getDuesDetails(regdNo));
    }

    @GetMapping("/get-basic-student-details/{regdNo}")
    public ResponseEntity<BasicStudentDto> getBasicStudentDetails(@PathVariable("regdNo") String regdNo) {
        return ResponseEntity.ok(accountSectionService.getBasicStudentDetails(regdNo));
    }

    @GetMapping("/get-fee-collection-history/{regdNo}/{feeTypes}")
    public ResponseEntity<FeeCollectionHistoryDto> getFeeCollectionHistory(@PathVariable("regdNo") String regdNo,@PathVariable("feeTypes") String feeTypes) {
        return ResponseEntity.ok(accountSectionService.getFeeCollectionByRegdNo(regdNo, FeeTypesType.fromDisplayName(feeTypes)));
    }

    @GetMapping("/get-fee-collection/{input}")
    public ResponseEntity<Collection<CollectionSummary>> getFeeCollectionBySessionId(@PathVariable("input") String input) {
        logger.info("get-fee-collection-by-sessionId/{}", input);
        int formatIndex = miscellaniousServices.checkFormat(input);
        switch (formatIndex) {
            case 1:
                return ResponseEntity.ok(accountSectionService.getAllDailyCollectionSummaryByPaymentDate(input));
            case 2:
                return ResponseEntity.ok(accountSectionService.getFeeCollectionsBySessionId(input));
            default:
                throw new RuntimeException("Should be unreachable");
        }
    }

    @GetMapping("/get-list-of-headers")
    public ResponseEntity<HeaderListDTO> getListOfHeaders() {
        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);

        // Submit tasks to executor
        Future<Set<String>> allSessions = executor.submit(() -> {
            try {
                return masterTableServicesImpl.getAllSessions();
            } finally {
                latch.countDown();  // Decrement latch when task is finished
            }
        });

        Future<Set<String>> allParticulars = executor.submit(() -> {
            try {
                return masterTableServicesImpl.getAllParticulars();
            } finally {
                latch.countDown();
            }
        });

        Future<Set<String>> allPaymentModes = executor.submit(() -> {
            try {
                return masterTableServicesImpl.getAllPaymentModes().stream()
                        .map(PaymentMode::getPmo)
                        .collect(Collectors.toSet());
            } finally {
                latch.countDown();
            }
        });

        try {        // Wait for all tasks to complete
            latch.await();

            // Retrieve the results from each Future
            // Return the response after all tasks are done
            return ResponseEntity.ok(new HeaderListDTO(allSessions.get(), allParticulars.get(), allPaymentModes.get()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-dashboard-data/{paymentDate}")
    public ResponseEntity<FeeDashboardSummary> getFeeDashboardData(@PathVariable("paymentDate") String paymentDate) {
        return ResponseEntity.ok(accountSectionService.getDashBoardNumbers(paymentDate));
    }

    @GetMapping("/get-collection-summary/{number}/{unit}")
    public ResponseEntity<Set<DailyCollectionSummary>> getCollectionSummary(@PathVariable("number") Integer number, @PathVariable("unit") String unit) {
        return ResponseEntity.ok(accountSectionService.collectionSummaryByTimePeriod(unit, number));
    }

    @GetMapping("/get-collection-report-by-date/{input}")
    public ResponseEntity<List<CollectionReportDTO>> fetchCollectionReportsByDate(@PathVariable("input") String input) {
        logger.info("get-collection-report-by-date/{}", input);
        int formatIndex = miscellaniousServices.checkFormat(input);
        return switch (formatIndex) {
            case 1 -> ResponseEntity.ok(accountSectionService.getCollectionReportByDate(input));
            case 3 -> {
                Date[] dates = miscellaniousServices.convertToDates(input);
                logger.info(dates.toString());
                yield ResponseEntity.ok(accountSectionService.getCollectionReportBetweenDates(dates[0], dates[1]));
            }
            default -> throw new RuntimeException("Should be unreachable");
        };
    }

    @GetMapping("/get-other-fees")
    public ResponseEntity<Set<String>> getAllOtherFeesDescriptions() {
        return ResponseEntity.ok(masterTableServicesImpl.getAllOtherFeesDescriptions());
    }

    @PostMapping("/get-feeTypes")
    public ResponseEntity<List<FeeTypesOnly>> saveFees(@RequestBody Set<FeeTypesType> feeTypes){
        return ResponseEntity.ok(masterTableServicesImpl.getAllFeeTypesForFeeAddition(feeTypes));
    }

    @PostMapping("/save-Fees")
    public ResponseEntity<List<Fees>> saveFeesToDatabase(@RequestBody List<Fees> fees){
        return ResponseEntity.ok(masterTableServicesImpl.saveFeesToDatabase(fees));
    }

    @PostMapping("/get-Fees-by-batch")
    public ResponseEntity<Set<FeesOnly>> getFeesByBatch(@RequestBody BasicFeeBatchDetails basicFeeBatchDetails){
        return ResponseEntity.ok(masterTableServicesImpl.getFeesByBatchId(basicFeeBatchDetails));
    }

    @PutMapping("/update-Fees")
    public ResponseEntity<List<Fees>> updateFees(@RequestBody Set<Fees> fees){
        return ResponseEntity.ok(masterTableServicesImpl.updateFees(fees));
    }

    @GetMapping("/get-money-receipt/{mrNo}")
    public ResponseEntity<MoneyReceipt> getMoenyReceipt(@PathVariable("mrNo") Long mrNo){
        return ResponseEntity.ok(feeCollectionTransactions.getMoneyReceiptByMrNo(mrNo));
    }
//
//    public ResponseEntity<Boolean> addFeeTypes(@RequestBody Set<FeeTypesOnly> feeTypes){
//
//    }
}


