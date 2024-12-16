package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.SemesterResult;
import com.trident.egovernance.global.helpers.SemesterResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SemesterResultRepository extends JpaRepository<SemesterResult, SemesterResultId> {

}
