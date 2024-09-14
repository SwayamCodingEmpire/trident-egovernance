package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.StudentType;
import com.trident.egovernance.helpers.TFWType;
import com.trident.egovernance.repositories.nsrRepositories.NSRRepository;
import com.trident.egovernance.repositories.permanentDB.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NSRServiceImpl implements NSRService {
    private final MapperServiceImpl mapperService;
    private final FeesRepository feesRepository;
    private final StandardDeductionFormatRepository standardDeductionFormatRepository;
    private final Logger logger = LoggerFactory.getLogger(NSRServiceImpl.class);
    private final NSRRepository nsrRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final StudentRepository studentRepository;
    public NSRServiceImpl(MapperServiceImpl mapperService, FeesRepository feesRepository, StandardDeductionFormatRepository standardDeductionFormatRepository, NSRRepository nsrRepository,DuesDetailsRepository duesDetailsRepository, StudentRepository studentRepository) {
        this.mapperService = mapperService;
        this.feesRepository = feesRepository;
        this.standardDeductionFormatRepository = standardDeductionFormatRepository;
        this.nsrRepository = nsrRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public NSRDto postNSRData(NSRDto nsrDto){
        if(!nsrRepository.existsById(nsrDto.getRollNo())){
            return mapperService.convertToNSRDto(nsrRepository.save(mapperService.convertToNSR(nsrDto)));
        }else {
            throw new RecordAlreadyExistsException("Record already exists");
        }
    }

    @Override
    @Cacheable(value = "nsr", key = "#rollNo")
    public NSRDto getNSRDataByRollNo(String rollNo) {
        logger.info("Fetching NSR data for roll no. "+rollNo);
        return mapperService.convertToNSRDto(nsrRepository.findById(rollNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }

//    public Set<NSRDto> getNSRDataByStudentName(String studentName) {
//        Set<NSR> nsr = nsrRepository.findAllByStudentName(studentName);
//        logger.info("NSR data fetched for student name "+studentName);
//        logger.info("NSR data fetched : "+nsr);
//        Set<NSRDto> nsrDtos = new HashSet<>();
//        nsr.forEach(nsr1 -> nsrDtos.add(mapperService.convertToNSRDto(nsr1)));
//        return nsrDtos;
//    }

    @Override
    public List<NSRDto> getAllNSRData() {
        List<NSR> nsr = StreamSupport.stream(nsrRepository.findAll().spliterator(), false).collect(Collectors.toList());
        List<NSRDto> nsrDtos = new ArrayList<>();
        nsr.forEach(nsr1 -> nsrDtos.add(mapperService.convertToNSRDto(nsr1)));
        return nsrDtos;
    }

    @Override
    public NSRDto getNSRDataByJeeApplicationNo(String jeeApplicationNo) {
        return mapperService.convertToNSRDto(nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }

    @Override
    public Boolean saveToPermanentDatabase(String jeeApplicationNo){
        try{
                NSR nsr = nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found"));
                PersonalDetails personalDetails = mapperService.convertToPersonalDetails(nsr);
                logger.info(nsr.toString());
                Student student = mapperService.convertToStudent(nsr);
                int dueYear = 0;
                if(student.getStudentType().equals(StudentType.REGULAR)){
                    dueYear = 1;
                }
                else{
                    dueYear = 2;
                }
                logger.info(student.toString());
                student.setBatchId(student.getCourse() + student.getAdmissionYear() + student.getBranchCode() + student.getStudentType());
                StudentAdmissionDetails studentAdmissionDetails = mapperService.convertToStudentAdmissionDetails(nsr);
                logger.info(studentAdmissionDetails.toString());
                StudentCareer studentCareer = mapperService.convertToStudentCareer(nsr);
                List<Fees> fees = feesRepository.findAllByBatchId(student.getBatchId());
                logger.info(fees.toString());
                List<DuesDetails> duesDetailsList = new ArrayList<>();
                for (Fees fee : fees) {
                    if(fee.getTfwType().equals(studentAdmissionDetails.getTfw())||fee.getTfwType().equals(TFWType.ALL)){
                        DuesDetails duesDetails = new DuesDetails();
                        duesDetails.setRegdNo(student.getRegdNo());
                        duesDetails.setDescription(fee.getDescription());
                        duesDetails.setBalanceAmount(fee.getAmount());
                        duesDetails.setAmountPaid(BigDecimal.ZERO);
                        duesDetails.setAmountDue(fee.getAmount());
                        duesDetails.setDeductionOrder(standardDeductionFormatRepository.findById(fee.getDescription()).orElseThrow(() -> new RecordNotFoundException("Record not found")).getDeductionOrder());
                        duesDetails.setDueYear(dueYear);
                        duesDetails.setSessionId(getSessionId());
                        duesDetails.setAmountPaidToJee(BigDecimal.ZERO);
                        duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
                        logger.info(duesDetails.toString());
                        duesDetailsList.add(duesDetails);
                    }
                }
                logger.info(duesDetailsRepository.saveAllAndFlush(duesDetailsList).toString());
                logger.info("Student Admission Details : {}", studentAdmissionDetails.toString());
                logger.info("Student Career : {}", studentCareer.toString());
                student.setStudentAdmissionDetails(studentAdmissionDetails);
                studentAdmissionDetails.setStudent(student);
                student.setPersonalDetails(personalDetails);
                personalDetails.setStudent(student);
                student.setStudentCareer(studentCareer);
                studentCareer.setStudent(student);
                logger.info("Student : {}", student);
                logger.info(studentRepository.save(student).toString());

                return true;
        }catch (Exception e){
            logger.error("Error occurred while saving to permanent database : "+e.getMessage());
            return false;

        }
    }

    private Boolean processDuesDetails(){
        return true;}

    private String getSessionId(){
        String currYear = String.valueOf(Year.now().getValue());
        String nextYear = String.valueOf(Year.now().getValue()+1);
        return currYear+"-"+nextYear;
    }
}
