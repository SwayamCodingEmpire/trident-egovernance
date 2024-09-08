package com.trident.egovernance.repositories.redisRepositories;

import com.trident.egovernance.entities.redisEntities.NSR;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NSRRepository extends CrudRepository<NSR,String> {
    Set<NSR> findAllByStudentName(String studentName);
    NSR findByApplicationNoAndRank(String applicationNo, Long rank);
}
