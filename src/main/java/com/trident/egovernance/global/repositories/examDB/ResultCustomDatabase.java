package com.trident.egovernance.global.repositories.examDB;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;

import java.sql.SQLException;
import java.util.List;

public interface ResultCustomDatabase {
    void invokeInsCustomDatabase(StudentExamResults studentExamResults) throws SQLException;

    // New method for batch insertion
    void invokeInsCustomDatabaseBatch(List<StudentExamResults> studentExamResultsList) throws SQLException;
}
