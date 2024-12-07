package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FeeCollectionRepository extends JpaRepository<FeeCollection,Long> {
    @Query("select MAX(f.mrNo) from FEECOLLECTION f")
    Long getMaxMrNo();


    @Query("SELECT f FROM FEECOLLECTION f LEFT JOIN FETCH f.mrDetails LEFT JOIN FETCH f.student WHERE f.student.regdNo = :regdNo")
    List<FeeCollection> findAllByStudent_RegdNo(String regdNo);

    @Query("SELECT new com.trident.egovernance.dto.RegdOnly(s.regdNo) FROM FEECOLLECTION f JOIN f.student s JOIN f.mrDetails m WHERE s.regdNo IN :regdNos AND m.particulars = :particulars")
    List<RegdOnly> findAllByStudent_RegdNoAAndMrDetails(Set<String> regdNo, String particulars);

    @Query("SELECT fc " +
            "FROM FEECOLLECTION fc " +
            "JOIN fc.mrDetails md " +
            "WHERE md.particulars = :particulars AND fc.sessionId = :sessionId AND fc.student.regdNo IN :regdNos")
    Set<FeeCollection> findAllByMrDetails_ParticularsAndSessionId(String particulars, String sessionId, Set<String> regdNos);

    @Modifying
    @Query("UPDATE FEECOLLECTION f SET f.dueYear = f.dueYear+1 WHERE f.mrNo IN :mrNos")
    long updateFeeCollectionByRegdNo(Set<String> mrNos);

    @Query("SELECT new com.trident.egovernance.dto.FeeCollectionDTOWithRegdNo(fc, s.regdNo) " +
            "FROM FEECOLLECTION fc " +
            "LEFT JOIN FETCH fc.mrDetails md " +
            "LEFT JOIN fc.student s " +
            "WHERE md.particulars = :particulars AND fc.sessionId = :sessionId AND s.regdNo IN :regdNos")
    Set<FeeCollectionDTOWithRegdNo> findAllByMrDetails_ParticularsAndSessionIdNew(String particulars, String sessionId, Set<String> regdNos);

    @Query("UPDATE FEECOLLECTION f SET f.dueYear = :year, f.sessionId = :sessionId WHERE f.mrNo IN :mrNos")
    int updateFeeCollectionByMrForHostelRegistered(Integer year, String sessionId, Set<Long> mrNos);

    Set<FeeCollection> findAllBySessionId(String sessionId);

    @Query("SELECT f FROM FEECOLLECTION f LEFT JOIN FETCH f.student LEFT JOIN FETCH f.mrDetails WHERE f.sessionId = :sessionId AND f.student.regdNo = :regdNo")
    Set<FeeCollection> findAllBySessionIdAndStudent_RegdNoWithAssociations(String sessionId, String regdNo);

    @Query("SELECT DISTINCT f FROM FEECOLLECTION f LEFT JOIN FETCH f.student LEFT JOIN FETCH f.mrNo WHERE f.student.regdNo = :regdNo")
    Set<FeeCollection> findAllByRegdNo(String regdNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM FEECOLLECTION f WHERE f.mrNo = :mrNo")
    int deleteByMrNo(Long mrNo);

    @Query("SELECT f.student.regdNo FROM FEECOLLECTION f WHERE f.mrNo = :mrNo")
    Optional<String> findRegdNoByMrNo(Long mrNo);

    @Query("SELECT f FROM FEECOLLECTION f LEFT JOIN FETCH f.mrDetails LEFT JOIN FETCH f.student WHERE f.mrNo = :mrNo")
    Optional<FeeCollection> findByMrNo(Long mrNo);

}
