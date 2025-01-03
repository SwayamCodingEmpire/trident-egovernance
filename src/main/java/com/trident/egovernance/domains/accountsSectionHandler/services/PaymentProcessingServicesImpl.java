package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.domains.nsrHandler.services.EmailSenderServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.exceptions.InvalidStudentException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.domains.accountsSectionHandler.PaymentProcessingServices;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.views.CurrentSession;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.views.CurrentSessionRepository;
import com.trident.egovernance.global.services.*;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
class PaymentProcessingServicesImpl implements PaymentProcessingServices {
    private final PDFGenerationService pdfGeneration;
    private final FeeCollectionTransactionServices feeCollectionTransactionServices;
    private final StudentRepository studentRepository;
    private final CurrentSessionFetcherServices currentSessionFetchingService;
    private final MasterTableServices masterTableServicesImpl;
    private final DuesDetailsRepository duesDetailsRepository;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(PaymentProcessingServicesImpl.class);
    private final FeeCollectionRepository feeCollectionRepository;
    private final FeeCollectionTransactions saveFeeCollection;
    private final CurrentSessionRepository currentSessionRepository;
    private final MapperServiceImpl mapperServiceImpl;
    private final EmailSenderServiceImpl emailSenderServiceImpl;
    private final PDFGenerationService pdfGenerationService;
    private final URLService urlService;

    public PaymentProcessingServicesImpl(PDFGenerationService pdfGeneration, FeeCollectionTransactionServices feeCollectionTransactionServices, StudentRepository studentRepository, MasterTableServices masterTableServicesImpl, CurrentSessionFetcherServices currentSessionFetchingService, DuesDetailsRepository duesDetailsRepository, EntityManager entityManager, FeeCollectionRepository feeCollectionRepository, FeeCollectionTransactions saveFeeCollection, CurrentSessionRepository currentSessionRepository, MapperServiceImpl mapperServiceImpl, EmailSenderServiceImpl emailSenderServiceImpl, PDFGenerationService pdfGenerationService, MoneyReceiptTokenGeneratorService moneyReceiptTokenGeneratorService, URLService urlService) {
        this.pdfGeneration = pdfGeneration;
        this.feeCollectionTransactionServices = feeCollectionTransactionServices;
        this.currentSessionFetchingService = currentSessionFetchingService;
        this.studentRepository = studentRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
        this.duesDetailsRepository = duesDetailsRepository;
        this.entityManager = entityManager;
        this.feeCollectionRepository = feeCollectionRepository;
        this.saveFeeCollection = saveFeeCollection;
        this.currentSessionRepository = currentSessionRepository;
        this.mapperServiceImpl = mapperServiceImpl;
        this.emailSenderServiceImpl = emailSenderServiceImpl;
        this.pdfGenerationService = pdfGenerationService;
        this.urlService = urlService;
    }


    public Pair<MoneyReceipt,StudentBasicDTO> processPaymentAutoMode(FeeCollection feeCollection, String regdNo, boolean isUpdate) {
        Student student = studentRepository.findById(regdNo).orElseThrow(() -> new InvalidStudentException("Invalid Registration Number"));


        if (feeCollection.getCollectedFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputsException("Invalid Collected Fee");
        }
        feeCollection.setStudent(student);
        feeCollection.setDueYear(student.getCurrentYear());
        feeCollection.setPaymentDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        List<DuesDetails> duesDetails = duesDetailsRepository.findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(regdNo, BigDecimal.ZERO);

        if (!isUpdate) {
            long mrNo = feeCollectionRepository.getMaxMrNo() + 1;
            feeCollection.setMrNo(mrNo);
        }
        PaymentProcessingInternalData processedData = processPayment(feeCollection, student, regdNo, duesDetails, feeCollection.getCollectedFee(), 1);
        FeeCollection processedFeeCollection = processedData.feeCollection();
        List<DuesDetails> processedDuesDetails = processedData.duesDetails();
        Set<MrDetails> processedMrDetails = processedData.mrDetails();
        if (processedData.collectedFees().compareTo(BigDecimal.ZERO) > 0) {
            DuesDetails duesDetail = createExcessDues(regdNo, processedData);
            processedMrDetails.add(createMrDetails(duesDetail, processedData.collectedFees(), processedData.slNo(), processedFeeCollection));
            processedDuesDetails.add(duesDetail);
        }
        logger.info(processedDuesDetails.toString());
        processedFeeCollection.setMrDetails(processedMrDetails);
        logger.info(feeCollection.toString());
        logger.info(feeCollection.getMrDetails().toString());
        return Pair.of(saveFeeCollection.getMrDetailsSorted(processedFeeCollection, duesDetails),new StudentBasicDTO(student));
    }


    public Pair<MoneyReceipt,StudentBasicDTO> processPaymentNonAutoModes(FeeCollection feeCollection, String regdNo, boolean isUpdate) {
        Student student = studentRepository.findById(regdNo).orElseThrow(() -> new InvalidStudentException("Invalid Registration Number"));
        if (feeCollection.getCollectedFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputsException("Invalid Collected Fee");
        }
        if (!isUpdate) {
            long mrNo = feeCollectionRepository.getMaxMrNo() + 1;
            feeCollection.setMrNo(mrNo);
        }
        List<DuesDetails> duesDetailsOptionalFees = duesDetailsRepository.findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(regdNo, BigDecimal.ZERO);
        List<DuesDetails> duesDetailsCourseFee = duesDetailsOptionalFees.stream()
                .filter(duesDetails -> feeCollection.getFeeProcessingMode().equals(FeeProcessingMode.COURSEFEES) ? duesDetails.getFeeType().getFeeGroup().compareTo("COURSEFEE") == 0 : duesDetails.getFeeType().getType().equals(FeeTypesType.OPTIONAL_FEES))
                .collect(Collectors.toCollection(ArrayList::new));
        duesDetailsOptionalFees.removeAll(duesDetailsCourseFee);
        feeCollection.setPaymentDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        feeCollection.setDueYear(student.getCurrentYear());
        PaymentProcessingInternalData processedData = processPayment(feeCollection, student, regdNo, duesDetailsCourseFee, feeCollection.getCollectedFee(), 1);
        FeeCollection processedFeeCollection = processedData.feeCollection();
        logger.info(processedFeeCollection.toString());
        List<DuesDetails> processedDuesDetails = processedData.duesDetails();
        Set<MrDetails> processedMrDetails = processedData.mrDetails();
        if (processedData.collectedFees().compareTo(BigDecimal.ZERO) > 0) {
            PaymentProcessingInternalData processedDataOptionalFees = processPayment(feeCollection, student, regdNo, duesDetailsOptionalFees, processedData.collectedFees(), processedData.slNo());
            processedFeeCollection = processedDataOptionalFees.feeCollection();
            processedDuesDetails.addAll(processedDataOptionalFees.duesDetails());
            processedMrDetails.addAll(processedDataOptionalFees.mrDetails());
            if (processedDataOptionalFees.collectedFees().compareTo(BigDecimal.ZERO) > 0) {
                logger.info(processedFeeCollection.toString());
                DuesDetails excessDues = createExcessDues(regdNo, processedDataOptionalFees);
                processedDuesDetails.add(excessDues);
                processedMrDetails.add(createMrDetails(excessDues, processedData.collectedFees(), processedData.slNo(), processedFeeCollection));
                logger.info(processedMrDetails.toString());
            }
        }
        processedFeeCollection.setMrDetails(processedMrDetails);
        return Pair.of(saveFeeCollection.getMrDetailsSorted(processedFeeCollection, processedDuesDetails),new StudentBasicDTO(student));
    }

    @Override
    public MoneyReceipt processPaymentInterface(FeeCollection feeCollection, String regdNo, boolean isUpdate) {
        try{
            MoneyReceipt moneyReceipt = new MoneyReceipt();
            Pair<MoneyReceipt, StudentBasicDTO> processedPayment;
            if (feeCollection.getFeeProcessingMode().equals(FeeProcessingMode.AUTO)) {
                processedPayment = processPaymentAutoMode(feeCollection, regdNo, false);
                moneyReceipt = processedPayment.getLeft();
            } else {
                processedPayment = processPaymentNonAutoModes(feeCollection, regdNo, false);
                moneyReceipt = processedPayment.getLeft();
            }
            String url = urlService.generateUrl(processedPayment.getLeft().getMrNo());

            logger.info("The url is: " + url);
            logger.info("Sending email");
//            byte[] pdfResponse = pdfGeneration.generatePdf();
            emailSenderServiceImpl.sendPaymentReceiptEmail(
                    processedPayment.getRight().email(),
                    processedPayment.getLeft().getMrNo(),
                    processedPayment.getRight().studentName(),
                    processedPayment.getLeft().getPaymentDuesDetails().totalPaid(),
                    "8888888888",
                    new PDFObject(studentRepository.findBasicStudentData(regdNo), moneyReceipt,url));
            return moneyReceipt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Transactional
//    @Override
//    public MoneyReceipt updateFeesCollection(FeeCollection feeCollection) {
//        FeeCollection feeCollection1 = feeCollectionRepository.findByMrNo(feeCollection.getMrNo()).orElseThrow(() -> new InvalidInputsException("Invalid Money Receipt Number"));
//        if (feeCollectionTransactionServices.deleteFeeCollectionRecord(feeCollection1) > 0) {
//            if (feeCollection.getFeeProcessingMode() == null) {
//                return processOtherFeesPayment(
//                        new OtherFeesPayment(
//                                feeCollection.getMrNo(),
//                                new OtherFeeCollection(
//                                        feeCollection.getCollectedFee(),
//                                        feeCollection.getPaymentMode()
//                                ),
//                                mapperServiceImpl.convertToMrDetailsDTOSet(feeCollection.getMrDetails())
//                        ),
//                        feeCollection1.getStudent().getRegdNo(),
//                        true);
//            } else if (feeCollection.getFeeProcessingMode().equals(FeeProcessingMode.AUTO)) {
//                return processPaymentAutoMode(feeCollection, feeCollection1.getStudent().getRegdNo(), true);
//            } else {
//                return processPaymentNonAutoModes(feeCollection, feeCollection1.getStudent().getRegdNo(), true);
//            }
//        } else {
//            throw new RecordNotFoundException("Invalid Fee Collection");
//        }
//    }


    public boolean deleteFeeCollection(Long mrNo) {
        FeeCollection feeCollection = feeCollectionRepository.findByMrNo(mrNo).orElseThrow(()-> new InvalidInputsException("Invalid Fee Collection"));
        return feeCollectionTransactionServices.deleteFeeCollectionRecord(feeCollection)>0;
    }

    @Override
    public MoneyReceipt processOtherFessPaymentInterface(OtherFeesPayment otherFeesPayment, String regdNo, boolean isUpdate) {
        try{
            MoneyReceipt moneyReceipt = new MoneyReceipt();
            Pair<MoneyReceipt, StudentBasicDTO> processedPayment;
                processedPayment = processOtherFeesPayment(otherFeesPayment, regdNo, false);
                moneyReceipt = processedPayment.getLeft();
            String url = urlService.generateUrl(processedPayment.getLeft().getMrNo());

            logger.info("The url is: " + url);
            logger.info("Sending email");
//            byte[] pdfResponse = pdfGeneration.generatePdf();
            emailSenderServiceImpl.sendPaymentReceiptEmail(
                    processedPayment.getRight().email(),
                    processedPayment.getLeft().getMrNo(),
                    processedPayment.getRight().studentName(),
                    processedPayment.getLeft().getPaymentDuesDetails().totalPaid(),
                    "8888888888",
                    new PDFObject(studentRepository.findBasicStudentData(regdNo), moneyReceipt,url));
            return moneyReceipt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Pair<MoneyReceipt,StudentBasicDTO> processOtherFeesPayment(OtherFeesPayment otherFeesPayment, String regdNo, boolean isUpdate) {
        logger.info(otherFeesPayment.toString());
        Student student = studentRepository.findById(regdNo).orElseThrow(() -> new InvalidStudentException("Invalid Registration Number"));
        Long mrNo = 0L;
        if (isUpdate) {
            mrNo = otherFeesPayment.mrNo();
        } else {
            mrNo = feeCollectionRepository.getMaxMrNo() + 1;
        }
        FeeCollection feeCollection = new FeeCollection(otherFeesPayment.feeCollection());
        feeCollection.setPaymentDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        feeCollection.setMrNo(mrNo);
        CurrentSession currentSession = currentSessionRepository.findById(regdNo).orElseThrow(() -> new InvalidStudentException("Invalid Registration Number"));
        feeCollection.setDueYear(currentSession.getCurrentYear());
        feeCollection.setSessionId(currentSession.getSessionId());
        feeCollection.setStudent(entityManager.getReference(Student.class, regdNo));
        Set<MrDetails> mrDetailsSet = otherFeesPayment.otherMrDetails().stream()
                .map(otherMrDetails -> new MrDetails(otherMrDetails, feeCollection))
                .collect(Collectors.toSet());
        feeCollection.setMrDetails(mrDetailsSet);
        FeeCollection savedFeeCollection = feeCollectionRepository.save(feeCollection);
        MoneyReceipt moneyReceipt = saveFeeCollection.sortMrDetailsByMrHead
                (
                        savedFeeCollection.getMrDetails().stream()
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
                        new FeeCollectionDetails(savedFeeCollection),
                        null
                );
        logger.info(moneyReceipt.toString());
        return Pair.of(moneyReceipt,new StudentBasicDTO(student));
    }

    @Override
    public PaymentProcessingInternalData processPayment(FeeCollection feeCollection, Student student, String regdNo, List<DuesDetails> duesDetails, BigDecimal collectedFees, long slNo) {
        feeCollection.setStudent(student);
        Set<MrDetails> mrDetailsList = new HashSet<>();

        logger.info("Dues details: ");
        logger.info(duesDetails.toString());
        for (DuesDetails duesDetail : duesDetails) {
            if (collectedFees.compareTo(BigDecimal.ZERO) > 0) {
                MrDetails mrDetails = createMrDetails(duesDetail, collectedFees, slNo, feeCollection);
                collectedFees = updateDuesAndCollectedFees(duesDetail, collectedFees);
                mrDetails.setFeeCollection(feeCollection);
                mrDetailsList.add(mrDetails);
            } else if (collectedFees.compareTo(BigDecimal.ZERO) == 0) {
                logger.info("Collected fees reached 0");
                break;
            }
            slNo++;
        }
        feeCollection.setSessionId(currentSessionFetchingService.fetchCurrentSessionForStudent(regdNo));
        return new PaymentProcessingInternalData(
                mrDetailsList,
                feeCollection,
                collectedFees,
                duesDetails,
                slNo
        );
    }


    private MrDetails createMrDetails(DuesDetails duesDetails, BigDecimal collectedFees, long slNo, FeeCollection feeCollection) {
        MrDetails mrDetails = new MrDetails();
        mrDetails.setSlNo(slNo);
        if (collectedFees.compareTo(BigDecimal.ZERO) == 0) {
            mrDetails.setAmount((duesDetails.getBalanceAmount()));
        } else if (duesDetails.getDescription().compareTo("EXCESS FEE") == 0) {
            mrDetails.setAmount(collectedFees);
        } else if (collectedFees.compareTo(duesDetails.getBalanceAmount()) < 0) {
            mrDetails.setAmount(collectedFees);
        } else {
            mrDetails.setAmount(duesDetails.getBalanceAmount());
        }
        mrDetails.setParticulars(duesDetails.getDescription());
        mrDetails.setFeeCollection(feeCollection);
        return mrDetails;
    }

    private BigDecimal updateDuesAndCollectedFees(DuesDetails duesDetail, BigDecimal collectedFees) {
        if (duesDetail.getDescription().compareTo("EXCESS FEE") == 0) {
            duesDetail.setAmountPaid(duesDetail.getAmountPaid().add(collectedFees));
            duesDetail.setBalanceAmount(duesDetail.getAmountDue().subtract(duesDetail.getAmountPaid()));
            return BigDecimal.ZERO;
        } else if (collectedFees.compareTo(duesDetail.getBalanceAmount()) < 0) {
            logger.info("Collected fees: " + collectedFees);
            logger.info("Dues detail balance amount: " + duesDetail.getBalanceAmount());
            duesDetail.setBalanceAmount(duesDetail.getBalanceAmount().subtract(collectedFees));
            duesDetail.setAmountPaid(collectedFees);
            return BigDecimal.ZERO;
        } else {
            logger.info("Collected fees: " + collectedFees);
            logger.info("Dues detail balance amount: " + duesDetail.getBalanceAmount());
            BigDecimal remaining = collectedFees.subtract(duesDetail.getBalanceAmount());
            logger.info("Remaining amount: " + remaining);
            duesDetail.setAmountPaid(duesDetail.getBalanceAmount());
            duesDetail.setBalanceAmount(BigDecimal.ZERO);
            return remaining;
        }
    }

    private DuesDetails createExcessDues(String regdNo, PaymentProcessingInternalData processedData) {
        DuesDetails duesDetail = new DuesDetails();
        duesDetail.setId(-1L);
        duesDetail.setRegdNo(regdNo);
        duesDetail.setAmountDue(BigDecimal.ZERO);
        duesDetail.setAmountPaid(processedData.collectedFees());
        duesDetail.setBalanceAmount(duesDetail.getAmountDue().subtract(duesDetail.getAmountPaid()));
        duesDetail.setDescription("EXCESS FEE");
        duesDetail.setAmountPaidToJee(BigDecimal.ZERO);
        duesDetail.setDueDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
        duesDetail.setDueYear(processedData.feeCollection().getDueYear());
        duesDetail.setSessionId(processedData.feeCollection().getSessionId());
        duesDetail.setDeductionOrder(masterTableServicesImpl.getStandardDeductionFormat("EXCESS FEE").orElseThrow(() -> new RecordNotFoundException("Invalid Input")).getDeductionOrder());
        return duesDetail;
    }


}
