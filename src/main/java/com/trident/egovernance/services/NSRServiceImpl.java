package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.entities.reportingStudent.PersonalDetails;
import com.trident.egovernance.entities.reportingStudent.Student;
import com.trident.egovernance.entities.reportingStudent.StudentAdmissionDetails;
import com.trident.egovernance.entities.reportingStudent.StudentCareer;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.repositories.nsrRepositories.NSRRepository;
import com.trident.egovernance.repositories.reportingStudent.PersonalDetailsRepository;
import com.trident.egovernance.repositories.reportingStudent.StudentAdmissionDetailsRepository;
import com.trident.egovernance.repositories.reportingStudent.StudentCareerRepository;
import com.trident.egovernance.repositories.reportingStudent.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NSRServiceImpl implements NSRService {
    private final MapperServiceImpl mapperService;
    private final Logger logger = LoggerFactory.getLogger(NSRServiceImpl.class);
    private final NSRRepository nsrRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final StudentAdmissionDetailsRepository studentAdmissionDetailsRepository;
    private final StudentCareerRepository studentCareerRepository;
    private final StudentRepository studentRepository;
    public NSRServiceImpl(MapperServiceImpl mapperService, NSRRepository nsrRepository, PersonalDetailsRepository personalDetailsRepository, StudentAdmissionDetailsRepository studentAdmissionDetailsRepository, StudentCareerRepository studentCareerRepository, StudentRepository studentRepository) {
        this.mapperService = mapperService;
        this.nsrRepository = nsrRepository;
        this.personalDetailsRepository = personalDetailsRepository;
        this.studentAdmissionDetailsRepository = studentAdmissionDetailsRepository;
        this.studentCareerRepository = studentCareerRepository;
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

    public Boolean transferFromTempToPermanentSQLDatabase(String jeeApplicationNo){
        if(nsrRepository.existsById(jeeApplicationNo)){
            NSR nsr = nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found"));
            PersonalDetails personalDetails = mapperService.convertToPersonalDetails(nsr);
            Student student = mapperService.convertToStudent(nsr);
            StudentAdmissionDetails studentAdmissionDetails = mapperService.convertToStudentAdmissionDetails(nsr);
            StudentCareer studentCareer = mapperService.convertToStudentCareer(nsr);
            logger.info("Student : {}",student.toString());
            logger.info("Student Admission Details : {}", studentAdmissionDetails.toString());
            logger.info("Student Career : {}", studentCareer.toString());
            return true;
        }else {
            throw new RecordNotFoundException("Record not found");
        }
    }
}
