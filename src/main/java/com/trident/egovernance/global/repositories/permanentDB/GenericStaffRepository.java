package com.trident.egovernance.global.repositories.permanentDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenericStaffRepository extends JpaRepository<Object, Integer> {
    @Query(value = "SELECT :fieldName f FROM :tableName t", nativeQuery = true)
    List<Object> getFieldValues(String tableName, String fieldName);
}
