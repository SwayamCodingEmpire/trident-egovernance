package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.DescriptionTypeSemester;
import com.trident.egovernance.dto.FeeGroupAndPartOfDTO;
import com.trident.egovernance.dto.FeeTypesOnly;
import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.helpers.FeeTypesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FeeTypesRepository extends JpaRepository<FeeTypes,String> {
//    List<FeeTypes> findByDescriptionIn(List<String> descriptions);

    List<FeeTypesMrHead> findByDescriptionIn(List<String> descriptions);

    @Query("SELECT f.description FROM FEETYPES f WHERE f.feeGroup = :feeGroup AND f.semester = :semester")
    String findDescriptionByFeeGroupAndSemester(@Param("feeGroup") String feeGroup, @Param("semester") int semester);

    FeeTypes findByFeeGroupAndSemester(String feeGroup, int semester);

    @Query("SELECT DISTINCT(f.description) FROM FEETYPES f")
    Set<String> findAllDescriptions();


    @Query("SELECT f.description FROM FEETYPES f WHERE f.type = :type")
    Set<String> findAllByType(FeeTypesType type);

    List<FeeTypesOnly> findAllByTypeIn(Set<FeeTypesType> feeTypes);

    List<FeeTypesOnly> findAllByFeeGroup(String feeGroup);

    Set<FeeTypesOnly> findAllBySemesterIn(Set<Integer> semesters);

    @Query("SELECT DISTINCT f.feeGroup, f.partOf FROM FEETYPES f")
    List<Object[]> findAllDistinctFeeGroupAndPartOfByDescription();

}
