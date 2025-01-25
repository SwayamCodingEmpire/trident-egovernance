package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.MrDetailsDTO;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MrDetailsRepository extends JpaRepository<MrDetails,Long> {
    @Query("select MAX(m.id) from MRDETAILS m")
    Long getMaxId();

//    @Modifying
//    @Query("UPDATE MRDETAILS m SET m.particulars = (" +
//            "SELECT f.description FROM FEETYPES f WHERE f.feeGroup = :feeGroup AND f.semester = (" +
//            "SELECT s.semester+1 FROM STUDENT s WHERE s.regdNo = m.feeCollection.student.regdNo))" +
//            "WHERE m.feeCollection.mrNo IN :mrNos AND m.particulars = :oldParticular")
//    int updateMrDetailsByIdAndParticular(Set<Long> mrNos, String oldParticular, String feeGroup);


//    int updateMrDetailsParticularsByMrNo(Long mrNo, String oldParticular, String);

    @Modifying
    @Query("UPDATE MRDETAILS m SET m.particulars = :description WHERE m.feeCollection.mrNo IN :mrNos")
    int updateMrDetailsByMrNoForHostelRegistered(String description, Set<Long> mrNos);

    @Query("SELECT new com.trident.egovernance.dto.MrDetailsDTO(m.slNo, m.particulars, m.amount) FROM MRDETAILS m LEFT JOIN m.feeCollection WHERE m.feeCollection.mrNo = :mrNo")
    List<MrDetailsDTO> findAllByMrNo(Long mrNo);
}
