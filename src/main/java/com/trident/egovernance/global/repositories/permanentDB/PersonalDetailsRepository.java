package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, String> {
    @Modifying
    @Transactional
    @Query("UPDATE PERSONAL_DETAILS p SET "
            + "p.fname = :fname, "
            + "p.mname = :mname, "
            + "p.lgName = :lgName, "
            + "p.permanentAddress = :permanentAddress, "
            + "p.permanentCity = :permanentCity, "
            + "p.permanentState = :permanentState, "
            + "p.permanentPincode = :permanentPincode, "
            + "p.parentContact = :parentContact, "
            + "p.parentEmailId = :parentEmailId, "
            + "p.presentAddress = :presentAddress, "
            + "p.district = :district "
            + "WHERE p.regdNo = :regdNo")
    int updatePersonalDetails(
            String fname,
            String mname,
            String lgName,
            String permanentAddress,
            String permanentCity,
            String permanentState,
            Long permanentPincode,
            String parentContact,
            String parentEmailId,
            String presentAddress,
            String district,
            String regdNo
    );

    PersonalDetails findByRegdNo(String regdNo);
}
