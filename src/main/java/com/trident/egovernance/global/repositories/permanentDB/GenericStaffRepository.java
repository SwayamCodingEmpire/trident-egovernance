package com.trident.egovernance.global.repositories.permanentDB;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenericStaffRepository{
    List<Object> getFieldValues(String tableName, String fieldName);
}
