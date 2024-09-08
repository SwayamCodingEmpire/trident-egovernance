package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.repositories.redisRepositories.NSRRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NSRServiceImpl implements NSRService {
    private final MapperServiceImpl mapperService;
    private final Logger logger = LoggerFactory.getLogger(NSRServiceImpl.class);
    private final NSRRepository nsrRepository;
    public NSRServiceImpl(MapperServiceImpl mapperService, NSRRepository nsrRepository) {
        this.mapperService = mapperService;
        this.nsrRepository = nsrRepository;
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
    public NSRDto getNSRDataByRollNo(String rollNo) {
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
}
