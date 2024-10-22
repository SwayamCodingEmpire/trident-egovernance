package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.OldDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldDiscountRepository extends JpaRepository<OldDiscount,Long> {
    @Modifying
    @Query(value = """
INSERT INTO OLDDISCOUNT (REGDNO, DISCOUNT, STAFFID, PARTICULARS, ID, COMMENTS, SESSIONID, REGDYEAR)
SELECT REGDNO, DISCOUNT, STAFFID, PARTICULARS, ID, COMMENTS, SESSIONID, REGDYEAR FROM DISCOUNT WHERE REGDNO IN (:regdNos)
""",nativeQuery = true)
    void saveToOldDiscount(@Param("regdNos") List<String> regdNos);
}
