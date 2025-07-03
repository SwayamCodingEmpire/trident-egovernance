package com.trident.egovernance.domains.resultUpload.services;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentExamResultRepositoryImpl implements ResultCustomDatabase {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(StudentExamResultRepositoryImpl.class);

    @Override
    public void invokeInsCustomDatabase(StudentExamResults studentExamResults) throws SQLException {
        // Custom logic for stored procedure, etc.
    }

    @Override
    public void invokeInsCustomDatabaseBatch(List<StudentExamResults> studentExamResultsList) throws SQLException {
        if (studentExamResultsList == null || studentExamResultsList.isEmpty()) {
            logger.warn("Attempted to process an empty batch. Skipping batch call.");
            return;
        }

        OracleCallableStatement callableStatement = null;
        Connection jdbcConnection = null; // This will be the unwrapped OracleConnection

        try {
            // Get the Hibernate Session from the EntityManager
            Session hibernateSession = entityManager.unwrap(Session.class);

            // Unwrap to get the native JDBC Connection, specifically OracleConnection
            jdbcConnection = hibernateSession.doReturningWork(
                    new ReturningWork<Connection>() {
                        @Override
                        public Connection execute(Connection connection) throws SQLException {
                            logger.info("Inside doReturningWork: Actual connection class: {}", connection.getClass().getName());
                            // It's safer to check if it's a wrapper for OracleConnection
                            if (connection.isWrapperFor(OracleConnection.class)) {
                                return connection.unwrap(OracleConnection.class);
                            } else {
                                // If not an OracleConnection, it might still work if underlying driver supports it
                                // but direct cast will fail. Log a warning or throw error if strict
                                logger.warn("Connection is not directly an OracleConnection. Attempting to proceed.");
                                return connection;
                            }
                        }
                    }
            );

            logger.info("After doReturningWork: Returned jdbcConnection class: {}", jdbcConnection.getClass().getName());

            // Ensure it's an OracleConnection for Oracle-specific types
            if (!(jdbcConnection instanceof OracleConnection)) {
                throw new SQLException("Underlying connection is not an OracleConnection. Cannot use Oracle-Specific types for batching.");
            }
            OracleConnection oracleConnection = (OracleConnection) jdbcConnection;


            // Create STRUCT descriptor for T_STUDENT_EXAM_RESULT_OBJ
            StructDescriptor structDescriptor = StructDescriptor.createDescriptor("T_STUDENT_EXAM_RESULT_OBJ", oracleConnection);
            List<STRUCT> structList = new ArrayList<>();

            // Populate STRUCT list from StudentExamResults objects
            for (StudentExamResults result : studentExamResultsList) {
                Object[] resultObject = new Object[]{
                        result.getRegdno(),
                        result.getSemester(),
                        result.getSubjectCode(),
                        result.getGrade(),
                        result.getCredits(),
                        result.getResultPublishDate()
                };

                structList.add(new STRUCT(structDescriptor, oracleConnection, resultObject));
            }

            // Create ARRAY descriptor for T_STUDENT_EXAM_RESULTS_TAB
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("T_STUDENT_EXAM_RESULTS_TAB", oracleConnection);
            ARRAY oracleArray = new ARRAY(arrayDescriptor, oracleConnection, structList.toArray());

            // Prepare and execute the CallableStatement
            // The '?' corresponds to the single IN parameter (the ARRAY)
            callableStatement = (OracleCallableStatement) oracleConnection.prepareCall("{call INSSEMRESULT_BATCH(?)}");
            callableStatement.setARRAY(1, oracleArray); // Set the ARRAY as the first parameter

            callableStatement.execute();

            logger.info("Batch stored procedure 'INSSEMRESULT_BATCH' invoked successfully for {} records", studentExamResultsList.size());

        } catch (SQLException e) {
            logger.error("Error invoking 'INSSEMRESULT_BATCH' procedure: {}", e.getMessage(), e);
            // Re-throw the SQLException to allow Spring Batch's transaction manager to handle rollback
            throw e;
        } finally {
            // Note: The connection is managed by Hibernate/Spring and should not be closed here.
            // Only close the CallableStatement.
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    logger.error("Error closing callable statement: {}", e.getMessage());
                }
            }
        }
    }
}
