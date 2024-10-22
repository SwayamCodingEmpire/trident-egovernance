package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.domains.accountsSectionHandler.AccountSectionService;
import com.trident.egovernance.dto.BasicStudentDto;
import com.trident.egovernance.dto.DuesDetailsDto;
import com.trident.egovernance.dto.DuesDetailsSortedDto;
import com.trident.egovernance.dto.FeeCollectionHistoryDto;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldDuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MapperServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountSectionServicesImpl implements AccountSectionService {
    private final MapperService mapperService;
    private final OldDuesDetailsRepository oldDuesDetailsRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final FeeCollectionRepository feeCollectionRepository;
    private final StudentRepository studentRepository;
    private final MapperServiceImpl mapperServiceImpl;

    public AccountSectionServicesImpl(MapperService mapperService, OldDuesDetailsRepository oldDuesDetailsRepository, DuesDetailsRepository duesDetailsRepository, FeeCollectionRepository feeCollectionRepository, StudentRepository studentRepository, MapperServiceImpl mapperServiceImpl) {
        this.mapperService = mapperService;
        this.oldDuesDetailsRepository = oldDuesDetailsRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.feeCollectionRepository = feeCollectionRepository;
        this.studentRepository = studentRepository;
        this.mapperServiceImpl = mapperServiceImpl;
    }


    @Override
    public DuesDetailsSortedDto getDuesDetails(String regdNo) {
        // Stream directly from repository calls
        Map<Integer, List<DuesDetailsDto>> duesGroupedByYear = Stream.concat(
                        duesDetailsRepository.findAllByRegdNoOrderByDeductionOrder(regdNo).stream(),
                        oldDuesDetailsRepository.findAllByRegdNo(regdNo).stream())
                .collect(Collectors.groupingBy(DuesDetailsDto::dueYear));
        return new DuesDetailsSortedDto(duesGroupedByYear.get(1),duesGroupedByYear.get(2),duesGroupedByYear.get(3),duesGroupedByYear.get(4));
    }
    @Override
    public BasicStudentDto getBasicStudentDetails(String regdNo){
        return studentRepository.findByRegdNo(regdNo);
    }

    @Override
    public FeeCollectionHistoryDto getFeeCollectionByRegdNo(String regdNo){
        List<FeeCollection> feeCollectionsList = feeCollectionRepository.findAllByStudent_RegdNo(regdNo);
        Map<Integer,List<FeeCollection>> feeCollectionGroupedByYear = feeCollectionsList
                .stream()
                .collect(Collectors.groupingBy(FeeCollection::getDueYear));
        return new FeeCollectionHistoryDto(feeCollectionGroupedByYear.get(1),feeCollectionGroupedByYear.get(2),feeCollectionGroupedByYear.get(3),feeCollectionGroupedByYear.get(4));
    }
}
