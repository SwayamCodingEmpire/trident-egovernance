package com.trident.egovernance.services;

import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.entities.redisEntities.StudentDocData;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperServiceImpl implements MapperService {
    private final ModelMapper modelMapper;

    public MapperServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.typeMap(NSR.class,StudentAdmissionDetails.class)
                .addMappings(mapper -> mapper.map(NSR::getTfw,StudentAdmissionDetails::setTfw));
    }

    @Override
    public NSRDto convertToNSRDto(NSR nsr) {
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

    public List<StudentDocs> convertToStudentDocs(List<StudentDocData> studentDocData) {
        return studentDocData.stream()
                .map(studentDocData1 -> modelMapper.map(studentDocData1,StudentDocs.class))
                .collect(Collectors.toList());
    }

    public List<StudentDocData> convertToStudentDocData(List<StudentDocs> studentDocs) {
        return studentDocs.stream()
                .map(studentDocs1 -> modelMapper.map(studentDocs1,StudentDocData.class))
                .collect(Collectors.toList());
    }
}
