package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.entities.redisEntities.StudentDocData;
import com.trident.egovernance.global.entities.views.CollectionReport;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.repositories.permanentDB.FeeTypesRepository;
import jakarta.persistence.Tuple;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperServiceImpl implements MapperService {
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(MapperServiceImpl.class);
    private final FeeTypesRepository feeTypesRepository;

    public MapperServiceImpl(ModelMapper modelMapper, FeeTypesRepository feeTypesRepository) {
        this.modelMapper = modelMapper;
        modelMapper.typeMap(NSR.class, StudentAdmissionDetails.class)
                .addMappings(mapper -> mapper.map(NSR::getTfw,StudentAdmissionDetails::setTfw));
        this.feeTypesRepository = feeTypesRepository;
    }
    @Override
    public NSRDto convertToNSRDtoList(NSR nsr) {
        return modelMapper.map(nsr,NSRDto.class);
    }

    @Override
    public NSR convertToNSR(NSRDto nsrDto) {
        return modelMapper.map(nsrDto,NSR.class);
    }

    @Override
    public PersonalDetails convertToPersonalDetails(NSR nsr) {
        return modelMapper.map(nsr,PersonalDetails.class);
    }

    @Override
    public Student convertToStudent(NSR nsr) {
        Student student = modelMapper.map(nsr, Student.class);
        if (student.getRegdNo() == null) {
            throw new IllegalArgumentException("Student registration number is required.");
        }
        return student;
    }

    @Override
    public StudentAdmissionDetails convertToStudentAdmissionDetails(NSR nsr) {
        return modelMapper.map(nsr,StudentAdmissionDetails.class);
    }

    @Override
    public StudentCareer convertToStudentCareer(NSR nsr) {
        return modelMapper.map(nsr,StudentCareer.class);
    }

    public Hostel convertToHostel(NSR nsr) {
        return modelMapper.map(nsr,Hostel.class);
    }

    public Transport convertToTransport(NSR nsr) {
        return modelMapper.map(nsr,Transport.class);
    }

    @Override
    public List<StudentDocs> convertToStudentDocs(List<StudentDocData> studentDocData) {
        return studentDocData.stream()
                .map(studentDocData1 -> modelMapper.map(studentDocData1,StudentDocs.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDocData> convertToStudentDocData(List<StudentDocs> studentDocs) {
        return studentDocs.stream()
                .map(studentDocs1 -> modelMapper.map(studentDocs1,StudentDocData.class))
                .collect(Collectors.toList());
    }

    public List<NSRDto> convertToNSRDtoList(List<NSR> nsrS){
        return nsrS.stream()
                .map(nsr -> convertToNSRDtoList(nsr))
                .toList();
    }

    @Override
    public Set<NSRDto> convertToNSRDtoSet(Set<NSR> nsrS) {
        return nsrS.stream()
                .map(nsr -> convertToNSRDtoList(nsr))
                .collect(Collectors.toSet());
    }

    public List<DuesDetailsDto> convertToDuesDetailsDto(List<DuesDetails> duesDetails) {
        logger.info(duesDetails.toString());
        return duesDetails.stream()
                .map(duesDetails1 -> new DuesDetailsDto(duesDetails1))
                .collect(Collectors.toList());
    }

    @Override
    public List<DuesDetailsDto> convertToDuesDetailsDtoFromOldDuesDetails(List<OldDueDetails> duesDetailsList) {
        logger.info(duesDetailsList.toString());
        return duesDetailsList.stream()
                .map(duesDetails1 -> new DuesDetailsDto(duesDetails1))
                .collect(Collectors.toList());
    }

    public Student convertToStudentFromStudentOnlyDTO(StudentOnlyDTO studentOnlyDTO) {
        return modelMapper.map(studentOnlyDTO,Student.class);
    }

    public StudentCareer covertToStudentCareerFromStudentCareerOnlyDTO(StudentCareerOnlyDTO studentCareerOnlyDTO) {
        return modelMapper.map(studentCareerOnlyDTO,StudentCareer.class);
    }

    public PersonalDetails convertToPersonalDetailsFromPersonalDetailsOnlyDTO(PersonalDetailsOnlyDTO personalDetailsOnlyDTO) {
        return modelMapper.map(personalDetailsOnlyDTO,PersonalDetails.class);
    }

    public StudentAdmissionDetails convertToStudentAdmissionDetailsFromStudentAdmissionDetailsOnlyDTO(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsOnlyDTO) {
        return modelMapper.map(studentAdmissionDetailsOnlyDTO,StudentAdmissionDetails.class);
    }

    public Hostel convertToHostelFromHostelOnlyDTO(HostelOnlyDTO hostelOnlyDTO) {
        return modelMapper.map(hostelOnlyDTO,Hostel.class);
    }

    public Transport convertToTransportFromTransportOnlyDTO(TransportOnlyDTO transportOnlyDTO) {
        return modelMapper.map(transportOnlyDTO,Transport.class);
    }

    public StudentDocs convertToStudentDocsFromStudentDocsOnlyDTO(StudentDocsOnlyDTO studentDocsOnlyDTO) {
        return modelMapper.map(studentDocsOnlyDTO,StudentDocs.class);
    }

    public Set<CollectionSummary> convertToCollectionSummarySet(Set<DailyCollectionSummary> dailyCollectionSummaries) {
        Set<CollectionSummary> collectionSummaries = new HashSet<>();
        for(DailyCollectionSummary dailyCollectionSummary : dailyCollectionSummaries) {
            collectionSummaries.add(new CollectionSummary(dailyCollectionSummary));
        }
        return collectionSummaries;
    }

//    @Override
//    public Set<String> getListOfOtherFees() {
//        feeTypesRepository.
//    }

    public List<CollectionReportDTO> convertFromTuplesToListOfCollectionReportDTO(List<Tuple> tuples){
        Map<CollectionReport, List<MrDetailsDTO>> reportToDetailsMap = tuples.stream().collect(
                Collectors.groupingBy(
                        tuple -> tuple.get(0, CollectionReport.class), // Get the CollectionReport (first column in the tuple)
                        Collectors.mapping(tuple -> {
                            // Map each Tuple to MrDetailsDTO
                            long slNo = tuple.get(1, Long.class); // Get the slNo (second column)
                            String particulars = tuple.get(2, String.class); // Get the particulars (third column)
                            BigDecimal amount = tuple.get(3, BigDecimal.class); // Get the amount (fourth column)
                            return new MrDetailsDTO(slNo, particulars, amount);
                        }, Collectors.toList())
                )
        );
        // Convert the map entries to a list of CollectionReportDTO
        return reportToDetailsMap.entrySet().stream()
                .map(entry -> new CollectionReportDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Set<MrDetailsDTO> convertToMrDetailsDTOSet(Set<MrDetails> mrDetailsSet) {
        return mrDetailsSet.stream()
                .map(mrDetails -> new MrDetailsDTO(
                        mrDetails.getSlNo(),
                        mrDetails.getParticulars(),
                        mrDetails.getAmount()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FeesOnly> convertToFeesOnly(List<Fees> fees) {
        return fees.stream()
                .map(fees1 -> new FeesOnly(fees1))
                .collect(Collectors.toSet());
    }

    @Override
    public List<FeeTypes> convertToFeeTypesList(Set<FeeTypesOnly> feesList) {
        return feesList.stream()
                .map(FeeTypes::new)
                .toList();
    }

    @Override
    public Set<FeeTypesOnly> convertToFeeTypesOnlySet(List<FeeTypes> feesList) {
        return feesList.stream()
                .map(FeeTypesOnly::new)
                .collect(Collectors.toSet());
    }
    @Override
    public List<MrDetailsDto> convertToMrDetailsDtoSet(Set<MrDetails> mrDetailsSet) {
        return mrDetailsSet.stream()
                .map(MrDetailsDto::new)
                .collect(Collectors.toList());
    }
}
