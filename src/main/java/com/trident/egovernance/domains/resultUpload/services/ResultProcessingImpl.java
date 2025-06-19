package com.trident.egovernance.domains.resultUpload.services;

import com.trident.egovernance.exceptions.CsvProcessingException;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.helpers.CsvHelper;
import com.trident.egovernance.global.helpers.excelToCsvConverter;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@Service
public class ResultProcessingImpl implements ResultProcessingService, ResultCustomDatabase {

    private static Logger logger = LoggerFactory.getLogger(ResultProcessingImpl.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void processResultCSV(MultipartFile file, String examType, int semester, String branch) throws SQLException {
//        Here the .xlsx file is converted to .csv file

        if("Even".equalsIgnoreCase(examType) && semester % 2 != 0){
            logger.warn("Invalid semester {} for 'Even' exam type. Semester must be even.", semester);
            throw new IllegalArgumentException("Invalid semester for 'Even' exam type. Semester must be even.");
        }
        if("Odd".equalsIgnoreCase(examType) && semester % 2 == 0){
            logger.warn("Invalid semester {} for 'Odd' exam type. Semester must be odd.", semester);
            throw new IllegalArgumentException("Invalid semester for 'Odd' exam type. Semester must be odd.");
        }

        System.out.printf("Starting processing for: Branch=%s, Semester=%d, Type=%s, File=%s%n",
                branch, semester, examType, file.getOriginalFilename());

        MultipartFile csvFile = null;
        List<StudentExamResults> resultsToProcess;
        try{
            csvFile = excelToCsvConverter.convertExcelToCSV(file);
            logger.info("Successfully converted Excel file '{}' to temporary CSV file '{}'.",
                    file.getOriginalFilename(), csvFile.getOriginalFilename());
            resultsToProcess = CsvHelper.parseCsvToStudentResultsDTO(file.getInputStream());
            logger.info("Successfully parsed {} record from CSV file.", resultsToProcess.size());
        } catch (IOException e){
            logger.error("Error reading CSV file input stream: {}", e.getMessage(), e);
            throw new CsvProcessingException("Failed to read the uploaded CSV file.");
        } catch (CsvProcessingException e){
            throw e;
        } catch (Exception e){
            logger.error("An Unexpected error occurred while parsing the CSV file: {}", e.getMessage(), e);
            throw new CsvProcessingException("An Unexpected error occurred while parsing the CSV file.");
        }

        for(StudentExamResults result: resultsToProcess){
            try{
                invokeInsCustomDatabase(result);
            } catch (SQLException e){
                logger.error("Database error while processing record for regdNo {}: {}. Transaction will be rolled back.", result.getRegdno(), e.getMessage());
                throw e;
            }
        }
        logger.info("Finished processing for: Branch={}, Semester={}, Type={}, File={}. All records processed", branch, semester, examType, file.getOriginalFilename());

    }

    @Override
    public void invokeInsCustomDatabase(StudentExamResults studentExamResults) throws SQLException {
        try{
            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("INSSEMRESULT");

            storedProcedure.registerStoredProcedureParameter("p_regdNo", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_semester", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_subCode", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_subCredit", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_subGrade", String.class, ParameterMode.IN);

            storedProcedure.setParameter("p_regdNo", studentExamResults.getRegdno());
            storedProcedure.setParameter("p_semester", studentExamResults.getSemester());
            storedProcedure.setParameter("p_subCode", studentExamResults.getSubjectCode());
            storedProcedure.setParameter("p_subCredit", studentExamResults.getCredits());
            storedProcedure.setParameter("p_subGrade", studentExamResults.getGrade());
        } catch (Exception e){
            logger.error("Error invoking INSSEMRESULT procedure using StoredProcedureQuery: "+e.getMessage(), e);
            throw new SQLException("Failed to invoke INSSEMRESULT procedure"+e.getMessage(),e);
        }
    }
}
