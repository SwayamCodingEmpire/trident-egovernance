package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.StandardDeductionFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StandardDeductionFormatRepository extends JpaRepository<StandardDeductionFormat,String> {
    @Query("SELECT s FROM STANDARDDEDUCTIONFORMAT s WHERE s.description IN :descriptions")
    List<StandardDeductionFormat> findByDescriptions(@Param("descriptions") Set<String> descriptions);
}
