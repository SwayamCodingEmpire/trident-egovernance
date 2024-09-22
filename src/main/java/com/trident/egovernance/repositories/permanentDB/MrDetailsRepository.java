package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.MrDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MrDetailsRepository extends JpaRepository<MrDetails,Long> {
    @Query("select MAX(m.id) from MRDETAILS m")
    Long getMaxId();
}
