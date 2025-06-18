package com.trident.egovernance.global.helpers;

import com.trident.egovernance.dto.StudentExamResultsDTO;
import com.trident.egovernance.exceptions.CsvProcessingException;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvFormat;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {
    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

    // Define CSV Headers - these must match the column names in your CSV file
    // and ideally correspond to the fields in StudentExamResultsDTO record.
    public static final String[] HEADERS_ORDER = {
            "regdNo", "semester", "subjectCode", "credits", "grade"
            // If your CSV has more columns, add them here.
            // For the current INSSEMRESULT procedure, these are the relevant ones.
    };

    private static final String CSV_DELIMITER = ",";

    public static List<StudentExamResults> parseCsvToStudentResultsDTO(InputStream inputStream) {
        List<StudentExamResults> studentResults = new ArrayList<>();
        int rowNum = 0;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            String line;

            if((line = reader.readLine()) != null){
                rowNum++;
                logger.debug("Skipped CSV Header: {}", line);
            }

            while((line = reader.readLine()) != null){
                rowNum++;
                if(line.trim().isEmpty()){
                    logger.debug("Skipping empty line in a row {}.", rowNum);
                    continue;
                }

                String[] parts = line.split(CSV_DELIMITER, -1);

                if(parts.length != HEADERS_ORDER.length){
                    logger.error("Row {}: Mismatched column count. Expected {}, Found{}. Line: {}", rowNum, HEADERS_ORDER.length, parts.length, line);
                    throw new CsvProcessingException(
                            String.format("Error on row %d : Incorrect number of columns. Expected %d. ", rowNum, HEADERS_ORDER.length)
                    );
                }

                try{
                    StudentExamResults student = new StudentExamResults();

                    student.setRegdno(parts[0].trim());
                    student.setSemester(Integer.parseInt(parts[1].trim()));
                    student.setSubjectCode(parts[2].trim());
                    student.setCredits(Integer.parseInt(parts[3].trim()));
                    student.setGrade(parts[4].trim());

                    studentResults.add(student);

                } catch (NumberFormatException e){
                    logger.error("Row {}: Invalid number format. Line: '{}'. Error: {}", rowNum, line, e.getMessage());
                    throw new CsvProcessingException(String.format("Error on row %d: Invalid number format. Please check numeric fields.", rowNum));
                } catch (ArrayIndexOutOfBoundsException e){
                    logger.error("Row {}: Missing data for expected columns. Line: '{}'. Error: {}", rowNum, line, e.getMessage());
                    throw new CsvProcessingException(String.format("Error on row %d: Missing data in row. Check column count and content.", rowNum));
                } catch (Exception e){
                    logger.error("Row {}: Unexpected error parsing record. Line: '{}'. Error: {}", rowNum, line, e.getMessage());
                    throw new CsvProcessingException(String.format("Error on row %d: Failed to parse record. %s", rowNum, e.getMessage()));
                }
            }
            return studentResults;
        } catch (IOException e){
            logger.error("Error reading CSV file. Line: '{}'. Error: {}", rowNum, e.getMessage());
            throw new CsvProcessingException("Failed to read CSV file: " + e.getMessage());
        }

    }
}
