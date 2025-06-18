package com.trident.egovernance.global.repositories.examDB;

import com.trident.egovernance.dto.StudentExamResultsDTO;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;

import java.sql.SQLException;

public interface ResultCustomDatabase {
    void invokeInsCustomDatabase(StudentExamResults studentExamResults) throws SQLException;
}
