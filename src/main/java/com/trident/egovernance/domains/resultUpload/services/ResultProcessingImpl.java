package com.trident.egovernance.domains.resultUpload.services;

import com.trident.egovernance.exceptions.CsvProcessingException;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.helpers.CsvHelper;
import com.trident.egovernance.global.helpers.excelToCsvConverter;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import com.trident.egovernance.global.repositories.examDB.StudentMasterRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Service
public class ResultProcessingImpl implements ResultProcessingService, ResultCustomDatabase {
    @PersistenceContext
    private EntityManager entityManager;

    private final Map<String, Integer> subjectCodeToCreditsMap = new HashMap<>();

    private final StudentMasterRepo studentMasterRepo;

    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("^R[A-Z0-9]{4}\\d{3}$");

    private static Logger logger = LoggerFactory.getLogger(ResultProcessingImpl.class);

    public ResultProcessingImpl(StudentMasterRepo studentMasterRepo) {
        this.studentMasterRepo = studentMasterRepo;
    }

    public void loadSubjectMasterData(){
        logger.info("Loading subject master data from database using StudentMasterRepository...");
        try{
            List<Object[]> results = studentMasterRepo.findAllStudentCodesAndCredits();

            for(Object[] row : results) {
                String subCode = (String) row[0];
                Integer credits = ((Number) row[2]).intValue();
                subjectCodeToCreditsMap.put(subCode, credits);
            }
            logger.info("Successfully loaded {} subject records from database", results.size());
        } catch (RuntimeException e){
            logger.error("Failed to load subject master data from database. This will affect credit lookup! Error: {}",e.getMessage());
        }
    }

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

        logger.info("Starting Excel processing for: Branch={}, Semester={}, Type={}, File={}",
                branch, semester, examType, file.getOriginalFilename());

        if(subjectCodeToCreditsMap.isEmpty()){
            logger.warn("SubjectCodeToCreditsMap is empty. Skipping processing. Attempting to reload..");
            loadSubjectMasterData();
            if(subjectCodeToCreditsMap.isEmpty()) {
                throw new CsvProcessingException("Subject master data is empty. Cannot process results without valid subject definitions.");
            }
        }

        String resultPublishDate = examType + " (" + (LocalDate.now().getYear()) + "-" + (LocalDate.now().getYear()+1 % 100) + ")"; // e.g., "Odd (2024-25)"
//        // If you need "Odd-(2024-25)", you can derive it:
//        int currentYear = LocalDate.now().getYear();
//        String academicYear;
//        if ("Odd".equalsIgnoreCase(examType)) {
//            academicYear = String.format("%d-%d", currentYear, currentYear + 1);
//        } else { // Even
//            academicYear = String.format("%d-%d", currentYear - 1, currentYear);
//        }
//        // Example format "Odd-(2024-25)"
//        String respUpdateValue = String.format("%s-(%s)", examType, academicYear);


        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            if(!rowIterator.hasNext()){
                throw new CsvProcessingException("Excel file is empty or has no head row.");
            }
            Row headerRow = rowIterator.next();

            Map<Integer, String> columnMappings = new HashMap<>();

            int rollNoCol = -1;

            for(int i=0; i<headerRow.getLastCellNum(); i++){
                Cell cell = headerRow.getCell(i);
                if(cell != null && cell.getCellType() == CellType.STRING){
                    String header = cell.getStringCellValue().trim();
                    switch(header){
                        case "Roll No": rollNoCol = i;
                        break;
                        case "SGPA":
                        case "CGPA":
                        case "CREDIT":
                            logger.debug("Ignorring known non-subject header '{}' at index {}.", header, i);
                            break;
                        default:
                            if(!header.isEmpty() && header.length() == 8 && SUBJECT_CODE_PATTERN.matcher(header).matches()){
                                if(subjectCodeToCreditsMap.containsKey(header)){
                                columnMappings.put(i, header);
                                logger.debug("Identified valid subject column: {} at index {}", header, i);

                                }
                                else {
                                    logger.warn("Skipping header '{}' at index {} as it matches pattern but is NOT found in loaded subject master data. Please verify subject code in DB.", header, i);
                                }
                            } else if(!header.isEmpty()) {
                                logger.debug("Skipping header '{}' at index {} as it does not match subject code pattern or is a known fixed column.", header, i);
                            }
                            break;
                    }
                }
            }

            if(rollNoCol == -1){
                throw new CsvProcessingException("Required 'Roll No' column not found in Excel header.");
            }
            if (columnMappings.isEmpty()) {
                throw new CsvProcessingException("No valid subject code columns found in Excel sheet matching pattern and recognized in database.");
            }

            logger.info("Identified 'Roll No' column at index: {}", rollNoCol);
            logger.info("Dynamically identified and validated subject columns for parsing: {}", columnMappings.values());

//            Iterate through to extract the students data
            int rowNumber = 1;
            while(rowIterator.hasNext()){
                Row dataRow = rowIterator.next();
                rowNumber++;

                Cell rollNoCell = dataRow.getCell(rollNoCol);
                if(rollNoCell != null && rollNoCell.getCellType() == CellType.STRING){
                    logger.warn("Skipping empty or malformed row at index {} (Roll No missing or invalid).", rowNumber);
                    continue;
                }

                String regdNo = "";
                if(rollNoCell.getCellType() == CellType.NUMERIC){
                    DataFormatter dataFormatter = new DataFormatter();
                    regdNo = dataFormatter.formatCellValue(rollNoCell);
                } else if (rollNoCell.getCellType() == CellType.STRING){
                    regdNo = rollNoCell.getStringCellValue().trim();
                } else {
                    logger.warn("Row {}: Roll No cell at index {} has an unexpected type ({}). Skipping row.", rowNumber, rollNoCol, rollNoCell.getCellType());
                    continue;
                }

                if(regdNo.isEmpty()){
                    logger.warn("Row {}: Roll No is empty after parsing for student. Skipping row.", rowNumber);
                    continue;
                }

                for(Map.Entry<Integer, String> entry : columnMappings.entrySet()){
                    int subjectColIndex = entry.getKey();
                    String subjectCode = entry.getValue();

                    Cell gradeCell = dataRow.getCell(subjectColIndex);

                    if(gradeCell != null && gradeCell.getCellType() == CellType.BLANK){
                        String grade = "";
                        try{
                            if(gradeCell.getCellType() == CellType.NUMERIC){
                                grade = gradeCell.getStringCellValue().trim();
                            } else if(gradeCell.getCellType() == CellType.NUMERIC){
                                DataFormatter formatter = new DataFormatter();
                                grade = formatter.formatCellValue(gradeCell);
                            } else if(gradeCell.getCellType() == CellType.FORMULA){
                                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                grade = new DataFormatter().formatCellValue(gradeCell, evaluator);
                            }
                        } catch(IllegalStateException e){
                            logger.warn("Row {}: Cell at column {} (Subject: {}) for RegNo {} has an unexpected type ({}). Skipping grade for this subject. Error: {}", rowNumber, subjectColIndex, subjectCode, regdNo, gradeCell.getCellType(), e.getMessage());
                            continue;
                        }

                        if(grade.isEmpty() || "S".equalsIgnoreCase(grade) || "-".equalsIgnoreCase(grade) || "NA".equalsIgnoreCase(grade)){
                            logger.debug("Row {}: Skipping grade '{}' for subject '{}' (RegNo: {}) as it's an empty, formula, or special grade.", rowNumber, grade, subjectCode, regdNo);
                            continue;
                        }

//                        ----Retrieve Credits from the preloaded map----
                        Integer credits = subjectCodeToCreditsMap.get(subjectCode);
                        if (credits == null) {
                            logger.error("INTERNAL ERROR: Credits not found in map for validated subject '{}'. This should not happen. Skipping record for RegNo {}.", subjectCode, regdNo);
                        }

                        // Create StudentExamResults entity directly
                        StudentExamResults studentResult = new StudentExamResults(
                                regdNo,
                                semester,
                                subjectCode,
                                grade, // This is the grade
                                credits,
                                resultPublishDate // Use the derived result publish date
                        );

                        try{
                            invokeInsCustomDatabase(studentResult);
                        } catch (SQLException e){
                            logger.error("Database error while processing record for regdNo {} subject {}: {}. Transaction will be rolled back.", regdNo, subjectCode, e.getMessage());
                            throw e;
                        }
                    }
                }
            }
            logger.info("Finished Excel processing for: Branch={}, Semester={}, Type={}, File={}. All records processed.",
                    branch, semester, examType, file.getOriginalFilename());
        } catch (IOException e){
            logger.error("Error reading CSV file input stream: {}", e.getMessage(), e);
            throw new CsvProcessingException("Failed to read the uploaded CSV file.");
        } catch (CsvProcessingException e){
            throw e;
        } catch (Exception e){
            logger.error("An Unexpected error occurred while parsing the CSV file: {}", e.getMessage(), e);
            throw new CsvProcessingException("An Unexpected error occurred while parsing the CSV file.");
        }
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
            storedProcedure.registerStoredProcedureParameter("p_resultPublishDate", String.class, ParameterMode.IN);

            storedProcedure.setParameter("p_regdNo", studentExamResults.getRegdno());
            storedProcedure.setParameter("p_semester", studentExamResults.getSemester());
            storedProcedure.setParameter("p_subCode", studentExamResults.getSubjectCode());
            storedProcedure.setParameter("p_subCredit", studentExamResults.getCredits());
            storedProcedure.setParameter("p_subGrade", studentExamResults.getGrade());
            storedProcedure.setParameter("p_resPubDate", studentExamResults.getResultPublishDate());

            storedProcedure.execute();
            logger.debug("Stored procedure 'INSSEMRESULT' invoked successfully for regdNo: {} subject: {}", studentExamResults.getRegdno(), studentExamResults.getSubjectCode());
        } catch (Exception e) {
            logger.error("Error invoking 'INSSEMRESULT' procedure for regdNo {} subject {}: {}",
                    studentExamResults.getRegdno(), studentExamResults.getSubjectCode(), e.getMessage(), e);
            throw new SQLException("Failed to invoke 'INSSEMRESULT' procedure for regdNo " +
                    studentExamResults.getRegdno() + " subject " + studentExamResults.getSubjectCode() + ": " + e.getMessage(), e);
        }
    }
}
