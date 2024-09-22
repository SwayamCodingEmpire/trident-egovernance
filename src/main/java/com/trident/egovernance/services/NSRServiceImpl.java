package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.BooleanString;
import com.trident.egovernance.helpers.RankType;
import com.trident.egovernance.helpers.StudentType;
import com.trident.egovernance.repositories.redisRepositories.NSRRepository;
import com.trident.egovernance.repositories.permanentDB.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Service
public class NSRServiceImpl implements NSRService {
    private final DuesProcessingService duesProcessingService;
    private final MapperServiceImpl mapperService;

    private final Logger logger = LoggerFactory.getLogger(NSRServiceImpl.class);
    private final NSRRepository nsrRepository;
    private final StudentRepository studentRepository;


    public NSRServiceImpl(DuesProcessingService duesProcessingService, MapperServiceImpl mapperService, NSRRepository nsrRepository, StudentRepository studentRepository) {
        this.duesProcessingService = duesProcessingService;
        this.mapperService = mapperService;
        this.nsrRepository = nsrRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public NSRDto postNSRData(NSR nsr){
//        if(!nsrRepository.existsById(nsr.getJeeApplicationNo())){
        if(nsr.getRankType().equals(RankType.JEE)){
            nsr.setAieeeRank(nsr.getRank().toString());
        }
        else {
            nsr.setOjeeRank(nsr.getRank().toString());
        }
            return mapperService.convertToNSRDto(nsrRepository.save(nsr));
//        }else {
//            throw new RecordAlreadyExistsException("Record already exists");
//        }
    }

    public NSRDto postNSRDataByStudent(NSR nsr){
        logger.info("Posting NSR data for student "+nsr.getJeeApplicationNo());
        return mapperService.convertToNSRDto(nsrRepository.save(nsr));
    }

    @Override
    @Cacheable(value = "nsr", key = "#rollNo")
    public NSRDto getNSRDataByRollNo(String rollNo) {
        logger.info("Fetching NSR data for roll no. "+rollNo);
        return mapperService.convertToNSRDto(nsrRepository.findById(rollNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }
    

    @Override
    public List<NSRDto> getAllNSRData() {
        List<NSR> nsr = StreamSupport.stream(nsrRepository.findAll().spliterator(), false).toList();
        List<NSRDto> nsrDtos = new ArrayList<>();
        nsr.forEach(nsr1 -> nsrDtos.add(mapperService.convertToNSRDto(nsr1)));
        return nsrDtos;
    }

    @Override
    public NSRDto getNSRDataByJeeApplicationNo(String jeeApplicationNo) {
        return mapperService.convertToNSRDto(nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }

    @Transactional
    @Override
    public Boolean saveToPermanentDatabase(String jeeApplicationNo){
        NSR nsr = nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found"));
        nsr.setBatchId(nsr.getCourse().getEnumName() + nsr.getAdmissionYear() + nsr.getBranchCode() + nsr.getStudentType());
        logger.info("Batch ID : {}",nsr.getBatchId());
        logger.info("Fetched from Redis");
        CompletableFuture<Boolean> processDues = duesProcessingService.initiateDuesDetails(nsr,nsr.getTfw());
        try{
            logger.info("Fetching from Redis");
            Student student = mapperService.convertToStudent(nsr);
            logger.info("NSR object : {}",nsr);
            StudentAdmissionDetails studentAdmissionDetails = mapperService.convertToStudentAdmissionDetails(nsr);
            StudentCareer studentCareer = mapperService.convertToStudentCareer(nsr);
            PersonalDetails personalDetails = mapperService.convertToPersonalDetails(nsr);
            student.setStudentAdmissionDetails(studentAdmissionDetails);
            studentAdmissionDetails.setStudent(student);
            student.setStudentCareer(studentCareer);
            studentCareer.setStudent(student);
            personalDetails.setStudent(student);
            student.setPersonalDetails(personalDetails);
            Transport transport = mapperService.convertToTransport(nsr);
            transport.setRegdyear(Year.now().getValue());
            Hostel hostel = mapperService.convertToHostel(nsr);
            if(nsr.getTransportOpted().equals(BooleanString.YES)){
                transport.setTransportAvailed(BooleanString.NO);
                transport.setRoute("N/A");
            }
            else {
                transport.setTransportAvailed(BooleanString.NO);
                transport.setPickUpPoint("N/A");
                transport.setRoute("N/A");

            }
            hostel.setRegdyear(nsr.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
            transport.setRegdyear(nsr.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
            student.setTransport(transport);
            transport.setStudent(student);
            hostel.setHostelier(nsr.getHostelier());
            student.setHostel(hostel);
            hostel.setStudent(student);
//            student.setHostel(null);
//            student.setTransport(null);
            logger.info("Student object : {}",student);
            logger.info("Started saving to databse");
            studentRepository.save(student);
            logger.info("Saved to database");
            if(processDues.join()){
                return true;
            }
            else
            {
                throw new RuntimeException("Error occurred while processing dues details");
            }
        }catch (Exception e){
            processDues.cancel(true);
            throw new RuntimeException("Error occurred while saving to permanent database : "+e.getMessage());
        }
    }
}
