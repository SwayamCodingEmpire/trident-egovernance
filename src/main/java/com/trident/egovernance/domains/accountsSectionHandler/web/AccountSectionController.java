package com.trident.egovernance.domains.accountsSectionHandler.web;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.domains.accountsSectionHandler.services.FeeCollectionTransactions;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.AlterFeeCollection;
import com.trident.egovernance.global.entities.permanentDB.ExcessRefund;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.PaymentMode;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServicesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<StudentBasicDTO> getBasicStudentDetails(@PathVariable("regdNo") String regdNo) {
        return ResponseEntity.ok(accountSectionService.getBasicStudentDetails(regdNo));
    }

    @GetMapping("/get-fee-collection-history/{regdNo}/{feeTypes}")
    public ResponseEntity<FeeCollectionHistoryDto> getFeeCollectionHistory(@PathVariable("regdNo") String regdNo,@PathVariable("feeTypes") String feeTypes) {
        return ResponseEntity.ok(accountSectionService.getFeeCollectionByRegdNo(regdNo, FeeTypesType.fromDisplayName(feeTypes)));
    }

    @GetMapping("/get-fee-collection/{input}")
    public ResponseEntity<List<FeeCollectionOnlyDTO>> getFeeCollectionBySessionId(@PathVariable("input") String input) {
        logger.info("get-fee-collection-by-session/{}", input);
        int formatIndex = miscellaniousServices.checkFormat(input);
        return switch (formatIndex) {
            case 1 -> ResponseEntity.ok(accountSectionService.getFeeCollectionFilteredByPaymentDate(input));
            case 2 -> ResponseEntity.ok(accountSectionService.getFeeCollectionFilteredBySessionId(input));
            default -> throw new RuntimeException("Should be unreachable");
        };
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

    @PostMapping("/get-collection-report")
    public ResponseEntity<CollectionReportDTO> fetchCollectionReportsByDate(@RequestBody CollectionReportInputDTO collectionReportInputDTO) {
        logger.info("get-collection-report-by-date");
        if(collectionReportInputDTO.fromDate() != null){
            return ResponseEntity.ok(accountSectionService.getCollectionReportBetweenDates(collectionReportInputDTO.fromDate(), collectionReportInputDTO.toDate()));
        }
        else if(collectionReportInputDTO.session() != null){
            return ResponseEntity.ok(accountSectionService.getCollectionReportBySessionId(collectionReportInputDTO.session()));
        }
        else {
            throw new InvalidInputsException("Should be unreachable");
        }
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
    public ResponseEntity<List<Fees>> saveFeesToDatabase(@RequestBody FeesCRUDDto feesList){
        return ResponseEntity.ok(masterTableServicesImpl.saveFeesToDatabase(feesList));
    }

    @PostMapping("/get-Fees-by-batch")
    public ResponseEntity<Set<FeesOnly>> getFeesByBatch(@RequestBody BasicFeeBatchDetails basicFeeBatchDetails){
        return ResponseEntity.ok(masterTableServicesImpl.getFeesByBatchId(basicFeeBatchDetails));
    }

    @PutMapping("/update-Fees")
    public ResponseEntity<List<Fees>> updateFees(@RequestBody FeesCRUDDto feesList){
        return ResponseEntity.ok(masterTableServicesImpl.updateFees(feesList));
    }

    @GetMapping("/get-money-receipt/{mrNo}")
    public ResponseEntity<MoneyReceipt> getMoneyReceipt(@PathVariable("mrNo") Long mrNo){
        return ResponseEntity.ok(feeCollectionTransactions.getMoneyReceiptByMrNo(mrNo));
    }

    @GetMapping("/get-mrDetails-mrNo/{mrNo}")
    public ResponseEntity<List<MrDetailsDTO>> getMrDetails(@PathVariable("mrNo") Long mrNo){
        return ResponseEntity.ok(accountSectionService.fetchMrDetailsByMrNo(mrNo));
    }

    @GetMapping("/get-fines-list")
    public ResponseEntity<List<FeeTypesOnly>> getFines(){
        return ResponseEntity.ok(accountSectionService.getFines());
    }

    @GetMapping("/get-due-status-report")
    public ResponseEntity<List<DueStatusReport>> getDueStatusReport(@RequestParam("course") Optional<Courses> course, @RequestParam("branch") Optional<String> branch, @RequestParam("regdYear") Optional<Integer> regdYear){
        return ResponseEntity.ok(accountSectionService.fetchDueStatusReport(course, branch, regdYear));
    }
//
//    public ResponseEntity<Boolean> addFeeTypes(@RequestBody Set<FeeTypesOnly> feeTypes){
//
//    }

    @PostMapping("/edit-fee-collection")
    public ResponseEntity<Boolean> editFeeCollection(@RequestBody AlterFeeCollection feeCollection){
        return ResponseEntity.ok(accountSectionService.addToAlterQueue(feeCollection));
    }

    @GetMapping("/get-excess-fee-student-data")
    public ResponseEntity<ExcessFeeStudentData> getStudentWithExcessFee(@RequestParam("regdNo") String regdNo){
        return ResponseEntity.ok(accountSectionService.findStudentsWithExcessFee(regdNo));
    }

    @PostMapping("/refund-excess-fee")
    public void refundExcessFee(@RequestBody ExcessRefundDTO excessRefundDTO){
        accountSectionService.insertRefundData(new ExcessRefund(excessRefundDTO));
    }
}


