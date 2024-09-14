package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.StandardDeductionFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardDeductionFormatRepository extends JpaRepository<StandardDeductionFormat,String> {

}
