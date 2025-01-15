package com.trident.egovernance.domains.accountsSectionHandler.controllers;

import com.trident.egovernance.domains.accountsSectionHandler.services.AccountSectionService;
import com.trident.egovernance.domains.accountsSectionHandler.services.FeeCollectionTransactionsServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.AlterFeeCollection;
import com.trident.egovernance.global.entities.permanentDB.ExcessRefund;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.PaymentMode;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServicesImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts-section")
@Tag(name = "Accounts Section APIs", description = "APIs accessible by users with role ACCOUNTS and ADMIN only")
public class AccountSectionController {
    private final FeeCollectionTransactionsServiceImpl feeCollectionTransactionsServiceImpl;
    private final AccountSectionService accountSectionService;
    private final MiscellaniousServicesImpl miscellaniousServices;

    private final Logger logger = LoggerFactory.getLogger(AccountSectionController.class);
    private final MasterTableServicesImpl masterTableServicesImpl;
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public AccountSectionController(FeeCollectionTransactionsServiceImpl feeCollectionTransactionsServiceImpl, AccountSectionService accountSectionService, MiscellaniousServicesImpl miscellaniousServices, MasterTableServicesImpl masterTableServicesImpl) {
        this.feeCollectionTransactionsServiceImpl = feeCollectionTransactionsServiceImpl;
        this.accountSectionService = accountSectionService;
        this.miscellaniousServices = miscellaniousServices;
        this.masterTableServicesImpl = masterTableServicesImpl;
    }

    @Operation(summary = "Get Dues Details Data sorted and classified by regdYear and semester by inputting regdNo as a path variable", description = "Data source DuesDetails and OldDuesDetails")
    @GetMapping("/getDuesDetails/{regdNo}")
    public ResponseEntity<Map<Integer, Map<Integer, List<DuesDetailsDto>>>> getDuesDetails(@PathVariable("regdNo") String regdNo) {
        return ResponseEntity.ok(accountSectionService.getDuesDetails(regdNo));
    }

    @Operation(summary = "Get Basic Student Data by inputting regdNo as a path variable", description = "Data source Student Entity")
    @GetMapping("/get-basic-student-details/{regdNo}")
    public ResponseEntity<StudentBasicDTO> getBasicStudentDetails(@PathVariable("regdNo") String regdNo) {
        return ResponseEntity.ok(accountSectionService.getBasicStudentDetails(regdNo));
    }

    @Operation(summary = "Get Fee Collection History by inputting regdNo as a path variable along with FeeType", description = "This API can be used to get FeeCollectionHistory of OTHER FEES or FEES")
    @GetMapping("/get-fee-collection-history/{regdNo}/{feeTypes}")
    public ResponseEntity<FeeCollectionHistoryDto> getFeeCollectionHistory(@PathVariable("regdNo") String regdNo,@PathVariable("feeTypes") String feeTypes) {
        return ResponseEntity.ok(accountSectionService.getFeeCollectionByRegdNo(regdNo, FeeTypesType.fromDisplayName(feeTypes)));
    }

    @Operation(summary = "Get Fee Collection Data by certain date or by sessionId", description = "This API can be used to get Fee Collection Data by inputting date or sessionId in the specified format. The input would be via path variable")
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

    @Operation(summary = "Endpoint to get valid sessions, paymentModes, particulars from Database", description = "Endpoint to get valid sessions, paymentModes, particulars from Database")
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

    @Operation(summary = "Endpoint to Dashboard Data of Accounts section", description = "It takes the input of payment Date")
    @GetMapping("/get-dashboard-data/{paymentDate}")
    public ResponseEntity<FeeDashboardSummary> getFeeDashboardData(@PathVariable("paymentDate") String paymentDate) {
        return ResponseEntity.ok(accountSectionService.getDashBoardNumbers(paymentDate));
    }


    @GetMapping("/get-collection-summary/{number}/{unit}")
    public ResponseEntity<Set<DailyCollectionSummary>> getCollectionSummary(@PathVariable("number") Integer number, @PathVariable("unit") String unit) {
        return ResponseEntity.ok(accountSectionService.collectionSummaryByTimePeriod(unit, number));
    }

    @Operation(summary = "Endpoint to get collection Report", description = "It takes the input as a post request - fromDate, toDate, session")
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

    @Operation(summary = "Endpoint to get a set of desciptions(particulars)", description = "It results out a set of descriptions(String) having FeeType as OTHER FEES")
    @GetMapping("/get-other-fees")
    public ResponseEntity<Set<String>> getAllOtherFeesDescriptions() {
        return ResponseEntity.ok(masterTableServicesImpl.getAllOtherFeesDescriptions());
    }

//    @Operation(summary = "Endpoint to get a set of desciptions(particulars)", description = "It results out a set of descriptions(String) having FeeType as OTHER FEES")
//    @PostMapping("/get-feeTypes")
//    public ResponseEntity<List<FeeTypesOnly>> saveFees(@RequestBody Set<FeeTypesType> feeTypes){
//        return ResponseEntity.ok(masterTableServicesImpl.getAllFeeTypesForFeeAddition(feeTypes));
//    }

//    @PostMapping("/save-Fees")
//    public ResponseEntity<List<Fees>> saveFeesToDatabase(@RequestBody FeesCRUDDto feesList){
//        return ResponseEntity.ok(masterTableServicesImpl.saveFeesToDatabase(feesList));
//    }

    @Operation(summary = "Endpoint to get a set of Fees depending upon the input which is BatchId", description = "It results out a set of FeesOnly DTO")
    public ResponseEntity<Set<FeesOnly>> getFeesByBatch(@RequestBody BasicFeeBatchDetails basicFeeBatchDetails){
        return ResponseEntity.ok(masterTableServicesImpl.getFeesByBatchId(basicFeeBatchDetails));
    }

//    @PutMapping("/update-Fees")
//    public ResponseEntity<List<Fees>> updateFees(@RequestBody FeesCRUDDto feesList){
//        return ResponseEntity.ok(masterTableServicesImpl.updateFees(feesList));
//    }

    @Operation(summary = "Endpoint to get money Receipt", description = "It results out Money Receipt depending upon Mr No.")
    @GetMapping("/get-money-receipt/{mrNo}")
    public ResponseEntity<MoneyReceipt> getMoneyReceipt(@PathVariable("mrNo") Long mrNo){
        return ResponseEntity.ok(feeCollectionTransactionsServiceImpl.getMoneyReceiptByMrNo(mrNo));
    }

    @GetMapping("/get-mrDetails-mrNo/{mrNo}")
    public ResponseEntity<List<MrDetailsDTO>> getMrDetails(@PathVariable("mrNo") Long mrNo){
        return ResponseEntity.ok(accountSectionService.fetchMrDetailsByMrNo(mrNo));
    }

    @Operation(summary = "Endpoint to get List of Fine Fees", description = "It results out a List of FeesTypesOnly DTO")
    @GetMapping("/get-fines-list")
    public ResponseEntity<List<FeeTypesOnly>> getFines(){
        return ResponseEntity.ok(accountSectionService.getFines());
    }

    @Operation(summary = "Endpoint to get Due Status Report = List of DueStatusReport DTO", description = "It takes inputs a query params of branch, regdYear, course. Not giving a query param is equivalent to passing ALL")
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

    @Operation(summary = "Endpoint to get Student Data having excess fees", description = "It results out a  ExcessFeeStudentData DTO if the student has some excess fees")
    @GetMapping("/get-excess-fee-student-data")
    public ResponseEntity<ExcessFeeStudentData> getStudentWithExcessFee(@RequestParam("regdNo") String regdNo){
        logger.info("get-excess-fee-student-data");
        return ResponseEntity.ok(accountSectionService.findStudentsWithExcessFee(regdNo));
    }

    @Operation(summary = "Endpoint to refund Excess Fee of a student", description = "Its a post request")
    @PostMapping("/refund-excess-fee")
    public void refundExcessFee(@RequestBody ExcessRefundDTO excessRefundDTO){
        accountSectionService.insertRefundData(new ExcessRefund(excessRefundDTO));
    }

    @Operation(summary = "Endpoint to get Feetype list by inputting year as a query param", description = "It results a set of FeeTypesOnly")
    @GetMapping("/get-feeType-list")
    public ResponseEntity<Set<FeeTypesOnly>> getFeeTypeList(@RequestParam("year") Integer year){
        return ResponseEntity.ok(accountSectionService.getDescriptionByYear(year));
    }

    @Operation(summary = "Endpoint to create Fees", description = "Create records in Fees Tables")
    @PostMapping("/create-fees")
    public void createFees(@RequestBody FeesCRUDDto feesList){
        logger.info("create-fees {}", feesList.toString());
        accountSectionService.insertFees(feesList);
    }

    @Operation(summary = "Endpoint to create FeeTypes(Description)", description = "Create records in FeeTypes Tables")
    @PostMapping("/create-fee-types")
    public ResponseEntity<Set<FeeTypesOnly>> createFeeTypes(@RequestBody Set<FeeTypesOnly> feeTypes){
        return ResponseEntity.ok(masterTableServicesImpl.createNewFeeTypes(feeTypes));
    }
}


