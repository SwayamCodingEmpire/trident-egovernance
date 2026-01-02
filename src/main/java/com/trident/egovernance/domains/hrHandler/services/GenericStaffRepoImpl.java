package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.GenericStaffRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class GenericStaffRepoImpl implements GenericStaffRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object> getFieldValues(String tableName, String fieldName) {
        if (!isValidTable(tableName) || !isValidColumn(fieldName)) {
            throw new IllegalArgumentException("Invalid table name or column name");
        }

        String sql = "SELECT " + fieldName + " FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    private boolean isValidTable(String tableName) {
        return List.of("STAFFDEPT", "STAFFDESIGNATION", "STAFFCATEGORY", "STAFFSTATUS", "STAFFROLE").contains(tableName);
    }

    private boolean isValidColumn(String columnName) {
        return List.of("STAFF_DEPT", "STAFF_DESIGNATION", "CATEGORY", "STATUS", "ROLE").contains(columnName);
    }
}
