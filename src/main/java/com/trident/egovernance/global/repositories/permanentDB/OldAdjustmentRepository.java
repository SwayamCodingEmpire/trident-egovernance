package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.OldAdjustments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldAdjustmentRepository extends JpaRepository<OldAdjustments,String> {
    @Modifying
    @Query(value = """
INSERT INTO OLDADJUSTMENTS (ID,REGDNO, DESCRIPTION, ACTUALDUEAMOUNT, CONSIDERATIONAMOUNT, APPROVEDBY, SESSIONID, REGDYEAR)
SELECT ID,REGDNO, DESCRIPTION, ACTUALDUEAMOUNT, CONSIDERATIONAMOUNT, APPROVEDBY, SESSIONID, REGDYEAR FROM ADJUSTMENTS WHERE REGDNO IN (:regdNos)
""",nativeQuery = true)
    void saveAdjustmentsToOld(@Param("regdNos") List<String> regdNos);
}
