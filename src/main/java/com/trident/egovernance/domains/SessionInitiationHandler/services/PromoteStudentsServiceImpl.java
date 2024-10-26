package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class PromoteStudentsServiceImpl {
    private final StudentRepository studentRepository;

    public PromoteStudentsServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void promoteStudents(SessionInitiationDTO sessionInitiationDTO){
//        studentRepository.
    }
}
