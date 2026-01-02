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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement; // Keep this import for CallableStatement type
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException; // Import for the specific exception
import java.util.ArrayList;
import java.util.List;

@Repository
@Primary
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
        Connection jdbcConnection = null;

        try {
            Session hibernateSession = entityManager.unwrap(Session.class);

            jdbcConnection = hibernateSession.doReturningWork(
                    new ReturningWork<Connection>() {
                        @Override
                        public Connection execute(Connection connection) throws SQLException {
                            logger.info("Inside doReturningWork: Actual connection class: {}", connection.getClass().getName());
                            if (connection.isWrapperFor(OracleConnection.class)) {
                                return connection.unwrap(OracleConnection.class);
                            } else {
                                logger.warn("Connection is not directly an OracleConnection. Attempting to proceed.");
                                return connection;
                            }
                        }
                    }
            );

            logger.info("After doReturningWork: Returned jdbcConnection class: {}", jdbcConnection.getClass().getName());

            if (!(jdbcConnection instanceof OracleConnection)) {
                throw new SQLException("Underlying connection is not an OracleConnection. Cannot use Oracle-Specific types for batching.");
            }
            OracleConnection oracleConnection = (OracleConnection) jdbcConnection;


            StructDescriptor structDescriptor = StructDescriptor.createDescriptor("EXAM.T_STUDENT_EXAM_RESULT_OBJ", oracleConnection);
            List<STRUCT> structList = new ArrayList<>();

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

            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("EXAM.T_STUDENT_EXAM_RESULTS_TAB", oracleConnection);
            ARRAY oracleArray = new ARRAY(arrayDescriptor, oracleConnection, structList.toArray());

            callableStatement = (OracleCallableStatement) oracleConnection.prepareCall("{call EXAM.INSSEMRESULT_BATCH(?)}");
            callableStatement.setARRAY(1, oracleArray);

            callableStatement.execute();

            logger.info("Batch stored procedure 'INSSEMRESULT_BATCH' invoked successfully for {} records", studentExamResultsList.size());

        } catch (SQLIntegrityConstraintViolationException e) {
            // This block specifically catches unique constraint violations (ORA-00001)
            logger.error("Unique constraint violation detected while invoking 'INSSEMRESULT_BATCH' procedure: {}", e.getMessage(), e);

            // Log the regdno for all students in the failed batch.
            // Note: This cannot pinpoint the single exact duplicate, but identifies the set of records that were attempted to be inserted.
            StringBuilder regdNosInBatch = new StringBuilder("RegdNos in the failed batch: [");
            for (int i = 0; i < studentExamResultsList.size(); i++) {
                regdNosInBatch.append(studentExamResultsList.get(i).getRegdno());
                if (i < studentExamResultsList.size() - 1) {
                    regdNosInBatch.append(", ");
                }
            }
            regdNosInBatch.append("]");
            logger.error(regdNosInBatch.toString());

            // Re-throw the exception to allow Spring Batch's transaction manager to handle rollback
            throw e;
        }
        catch (SQLException e) {
            // This general catch-all handles other types of SQL exceptions
            logger.error("Error invoking 'INSSEMRESULT_BATCH' procedure: {}", e.getMessage(), e);
            // You might want to log all regdnos here too if other SQL errors should also list them
            // Or refine to specific SQL states if needed.
            throw e; // Re-throw to allow Spring Batch's transaction manager to handle rollback
        } finally {
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