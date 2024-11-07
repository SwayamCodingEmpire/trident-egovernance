package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.dto.FeeCollectionDTO;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.dto.SessionInitiationData;
import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromoteStudentsServiceImpl {
    private final StudentRepository studentRepository;
    private final FeeCollectionRepository feeCollectionRepository;

    public PromoteStudentsServiceImpl(StudentRepository studentRepository, FeeCollectionRepository feeCollectionRepository) {
        this.studentRepository = studentRepository;
        this.feeCollectionRepository = feeCollectionRepository;
    }

    public void promoteStudents(SessionInitiationData sessionInitiationDTO) {

    }
}
