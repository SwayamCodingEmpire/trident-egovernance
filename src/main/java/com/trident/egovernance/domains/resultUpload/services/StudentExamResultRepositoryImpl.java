package com.trident.egovernance.domains.resultUpload.services;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class StudentExamResultRepositoryImpl implements ResultCustomDatabase {

    private static final Logger logger = LoggerFactory.getLogger(StudentExamResultRepositoryImpl.class);

    @Override
    public void invokeInsCustomDatabase(StudentExamResults studentExamResults) throws SQLException {
        // Custom logic for stored procedure, etc.
    }
}
