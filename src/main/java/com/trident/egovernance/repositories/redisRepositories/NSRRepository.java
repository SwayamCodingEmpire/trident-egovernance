package com.trident.egovernance.repositories.redisRepositories;

import com.trident.egovernance.entities.redisEntities.NSR;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NSRRepository extends CrudRepository<NSR,String> {
//    Set<NSR> findAllByStudentName(String studentName);
}
