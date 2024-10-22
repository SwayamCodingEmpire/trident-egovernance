package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.OldHostel;
import com.trident.egovernance.global.helpers.BooleanString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldHostelRepository extends JpaRepository<OldHostel,String> {
    List<OldHostel> findByHostelOption(BooleanString hostelOption);
    @Modifying
    @Query(value = """
INSERT INTO OLDHOSTEL (REGDNO, HOSTELIER, HOSTELOPTION, HOSTELCHOICE, LGNAME, REGDYEAR) 
SELECT REGDNO, HOSTELIER, HOSTELOPTION, HOSTELCHOICE, LGNAME, REGDYEAR FROM HOSTEL WHERE REGDNO IN (:regdNos)
""",nativeQuery = true)
    void saveHostelToOld(@Param("regdNos")List<String> regdNos);
}
