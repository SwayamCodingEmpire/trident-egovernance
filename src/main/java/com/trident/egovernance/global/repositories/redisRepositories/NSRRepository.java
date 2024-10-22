package com.trident.egovernance.global.repositories.redisRepositories;

import com.trident.egovernance.global.entities.redisEntities.NSR;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NSRRepository extends CrudRepository<NSR,String> {
//    Set<NSR> findAllByStudentName(String studentName);
    Set<NSR> findAllByAdmissionYear(String admissionYear);
}
