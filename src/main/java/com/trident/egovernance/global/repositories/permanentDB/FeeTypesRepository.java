package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTypesRepository extends JpaRepository<FeeTypes,String> {
//    List<FeeTypes> findByDescriptionIn(List<String> descriptions);

    List<FeeTypesMrHead> findByDescriptionIn(List<String> descriptions);

    @Query("SELECT f.description FROM FEETYPES f WHERE f.feeGroup = :feeGroup AND f.semester = :semester")
    String findDescriptionByFeeGroupAndSemester(@Param("feeGroup") String feeGroup, @Param("semester") int semester);

    FeeTypes findByFeeGroupAndSemester(String feeGroup, int semester);


}
