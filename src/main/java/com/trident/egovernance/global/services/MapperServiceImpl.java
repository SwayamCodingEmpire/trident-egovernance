package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.entities.redisEntities.StudentDocData;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperServiceImpl implements MapperService {
    private final ModelMapper modelMapper;

    public MapperServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.typeMap(NSR.class, StudentAdmissionDetails.class)
                .addMappings(mapper -> mapper.map(NSR::getTfw,StudentAdmissionDetails::setTfw));
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

    public List<DuesDetailsDto> convertToDuesDetailsDto(List<BaseDuesDetails> baseDuesDetailsList) {
        return baseDuesDetailsList.stream()
                .map(duesDetails1 -> new DuesDetailsDto(
                        duesDetails1.getDescription(),
                        duesDetails1.getAmountDue(),
                        duesDetails1.getAmountPaid(),
                        duesDetails1.getAmountPaidToJee(),
                        duesDetails1.getBalanceAmount(),
                        duesDetails1.getDueYear()
                ))
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
}
