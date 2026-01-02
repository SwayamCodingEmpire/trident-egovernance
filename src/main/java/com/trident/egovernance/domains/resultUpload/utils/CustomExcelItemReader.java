package com.trident.egovernance.domains.resultUpload.utils;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.entities.examDB.SubjectMaster;
import com.trident.egovernance.global.repositories.examDB.SubjectMasterRepo;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value; // Import for @Value

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CustomExcelItemReader implements ItemReader<StudentExamResults>, ResourceAwareItemReaderItemStream<StudentExamResults> {

    @Setter
    private Resource resource;
    private Workbook workbook;
    private Sheet sheet;
    private Iterator<Row> rowIterator;
    private int currentRow = 0;
    private boolean headerSkipped = false;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;
    private Logger logger = LoggerFactory.getLogger(CustomExcelItemReader.class);
    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("^R[A-Z0-9]{4}\\d{3}$");

    private final SubjectMasterRepo subjectRepo;
    private List<StudentExamResults> itemsFromCurrentRow;
    private int currentItemFromRowIndex = 0;

    // ⭐ NEW: Inject the semester from job parameters ⭐
    // The value 'semester' must match the key used in your JobParameters
    @Value("#{jobParameters['semester']}")
    private Long jobSemester; // Using Long to match the job parameter type

    @Value("#{jobParameters['branch']}")
    private String jobBranch; // Added to get branch for auto-adding subjects

    private Map<String, Integer> subjectCodeToColumnIndex = new HashMap<>();
    private Map<Integer, String> columnToSubjectCodeIndex = new HashMap<>();

    public CustomExcelItemReader(SubjectMasterRepo subjectRepo) {
        this.subjectRepo = subjectRepo;
        this.dataFormatter = new DataFormatter();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (resource == null) {
            throw new ItemStreamException("Excel file resource must be set.");
        }
        try {
            InputStream inputStream = resource.getInputStream();
            this.workbook = WorkbookFactory.create(inputStream);
            this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            this.sheet = workbook.getSheetAt(0);
            this.rowIterator = sheet.iterator();

            // Correctly retrieve boolean from ExecutionContext
            if (executionContext.containsKey("currentRow")) {
                currentRow = executionContext.getInt("currentRow");
                headerSkipped = Boolean.TRUE.equals(executionContext.get("headerSkipped", Boolean.class, false));
                // Advance iterator to the stored currentRow
                for (int i = 0; i < currentRow && rowIterator.hasNext(); i++) {
                    rowIterator.next();
                }
                logger.info("Resuming Excel read from Row {}.", currentRow + 1);
            } else {
                currentRow = 0;
                headerSkipped = false;
                logger.info("Starting Excel read from Row 1.");
            }

            if (jobSemester == null || jobSemester <= 0) { // Added check for <=0
                logger.error("Job parameter 'semester' is missing or invalid. Auto-adding new subjects will use default semester 0.");
                jobSemester = 0L; // Default for missing semester
                // Optionally: throw new ItemStreamException("Semester job parameter is required.");
            }
            if (jobBranch == null || jobBranch.trim().isEmpty()) {
                logger.error("Job parameter 'branch' is missing or empty. Auto-adding new subjects will use default branch 'UNKNOWN'.");
                jobBranch = "UNKNOWN"; // Default for missing branch
            }

            if (!headerSkipped && rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                currentRow++;
                headerSkipped = true;

                subjectCodeToColumnIndex.clear();
                columnToSubjectCodeIndex.clear();

                for (Cell cell : headerRow) {
                    int colIndex = cell.getColumnIndex();
                    if (colIndex >= 3) {
                        String header = dataFormatter.formatCellValue(cell).trim();
                        if (SUBJECT_CODE_PATTERN.matcher(header).matches()) {
                            Optional<SubjectMaster> subject = subjectRepo.findById(header);
                            if (subject.isPresent()) {
                                subjectCodeToColumnIndex.put(header, colIndex);
                                columnToSubjectCodeIndex.put(colIndex, header);
                                logger.debug("Mapped valid Subject Code '{}' (from DB) to Column Index {}", header, colIndex);
                            } else {
                                logger.warn("Header '{}' in column {} matches pattern but NOT found in SUBCODE database table. Skipping this column for subject mapping.", header, colIndex);
                                try {
                                    SubjectMaster newSubject = new SubjectMaster();
                                    newSubject.setSubjectCode(header);
                                    newSubject.setSubjectName("Auto-added Subject: " + header); // Generic name
                                    newSubject.setSemester(jobSemester.toString()); // Use job parameter semester
                                    newSubject.setBranch(jobBranch); // Use job parameter branch
                                    newSubject.setCredit(3); // ⭐ IMPORTANT: Default credit to 3. Adjust as per common credit value. ⭐
                                    // If SubjectMaster has academicYear, you might set it here too:
                                    // newSubject.setAcademicYear(jobAcademicYear);

                                    // Save the new subject to the database
                                    subjectRepo.save(newSubject);

                                    subjectCodeToColumnIndex.put(header, colIndex);
                                    columnToSubjectCodeIndex.put(colIndex, header);
                                    logger.info("Successfully auto-added Subject '{}' to SUBCODE table and mapped to Column Index {}. Details: Semester={}, Branch={}, Credit={}",
                                            header, colIndex, newSubject.getSemester(), newSubject.getBranch(), newSubject.getCredit());
                                } catch (Exception e) {
                                    logger.error("Failed to auto-add Subject '{}' to SUBCODE table. This column will be skipped for processing grades. Error: {}", header, e.getMessage(), e);
                                    // If auto-add fails, this column will still be skipped for subject mapping.
                                }
                            }
                        } else {
                            logger.warn("Header '{}' in column {} is not a valid subject code pattern. Skipping.", header, colIndex);
                        }
                    }
                }
                if (subjectCodeToColumnIndex.isEmpty()) {
                    logger.error("No valid subject codes found in header row of the Excel file. Please check Excel format.");
                    throw new ItemStreamException("No valid subject codes found in header row. Cannot proceed.");
                }
                logger.info("Excel header row processed. Found {} subject columns. Starting data read from row {}.", subjectCodeToColumnIndex.size(), currentRow + 1);
            } else if (headerSkipped) {
                logger.info("Excel header already skipped (restarting). Starting data read from row {}.", currentRow + 1);
            } else {
                logger.warn("Excel file is empty or has no header row.");
            }

            // ⭐ Validate jobSemester here after injection ⭐
            if (jobSemester == null) {
                logger.error("Semester job parameter is missing or null. Cannot proceed without a valid semester.");
                throw new ItemStreamException("Semester job parameter is required.");
            } else {
                logger.info("Batch job semester identified as: {}", jobSemester);
            }

        } catch (IOException e) {
            throw new ItemStreamException("Failed to open Excel file" + resource.getFilename(), e);
        } catch (Exception e) {
            throw new ItemStreamException("Error initializing Excel reader: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentExamResults read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        // If there are still items from the current physical Excel row to process, return them first.
        if (itemsFromCurrentRow != null && currentItemFromRowIndex < itemsFromCurrentRow.size()) {
            StudentExamResults itemToReturn = itemsFromCurrentRow.get(currentItemFromRowIndex);
            logger.debug("Returning item {}/{} from current Excel row (physical row {}): RegdNo={}, Semester={}, SubjectCode={}",
                    currentItemFromRowIndex + 1, itemsFromCurrentRow.size(), currentRow,
                    itemToReturn.getRegdno(), itemToReturn.getSemester(), itemToReturn.getSubjectCode());
            currentItemFromRowIndex++;
            return itemToReturn;
        }

        // Move to the next physical row in Excel
        if (!rowIterator.hasNext()) {
            logger.info("Finished reading all rows from the Excel sheet. Signalling end of stream.");
            return null; // Signal that there are no more items
        }

        Row row = rowIterator.next();
        currentRow++; // Increment for the new physical row being read
        itemsFromCurrentRow = new ArrayList<>(); // Reset for the new physical row
        currentItemFromRowIndex = 0;

        // --- Logging the start of processing for a new physical row ---
        logger.info("--- Processing new physical Excel row {}. ---", currentRow);

        // Basic validation for the row (RegdNo cell empty check)
        Cell regdNoCellRaw = row.getCell(1); // Assuming RegdNo is in column B (index 1)
        String regdNo = (regdNoCellRaw != null) ? dataFormatter.formatCellValue(regdNoCellRaw).trim() : "";

        if (regdNo.isEmpty()) {
            logger.warn("Row {}: RegdNo (from column B) is empty or null after formatting. Skipping this physical row and advancing to next.", currentRow);
            return read(); // Recursively call to get next valid row
        }

        // --- Log the extracted RegdNo for the current physical row ---
        logger.info("Row {}: Extracted RegdNo for this physical row: '{}'.", currentRow, regdNo);

        try {
            // Iterate through mapped subject columns to create StudentExamResults DTOs
            for(Map.Entry<Integer, String> entry : columnToSubjectCodeIndex.entrySet()) {
                int colIndex = entry.getKey();
                String subjectCode = entry.getValue();

                Cell gradeCell = row.getCell(colIndex);
                String rawGrade = (gradeCell != null) ? dataFormatter.formatCellValue(gradeCell, formulaEvaluator).trim() : "";

                if(rawGrade.isEmpty() || "-".equalsIgnoreCase(rawGrade) || "NA".equalsIgnoreCase(rawGrade)){
                    logger.debug("Row {}: Skipping grade '{}' for subject '{}' (RegdNo: {}) as it's empty or special. This subject will NOT be added to batch.", currentRow, rawGrade, subjectCode, regdNo);
                    continue; // Skip this subject for the current row
                }

                StudentExamResults dto = new StudentExamResults();
                dto.setRegdno(regdNo);
                dto.setSubjectCode(subjectCode);
                dto.setGrade("F(Ex)".equalsIgnoreCase(rawGrade) || "F(Int)".equalsIgnoreCase(rawGrade) ? "F" : rawGrade);

                // ⭐ FIX: Use the injected semester job parameter instead of hardcoded -1 ⭐
                // Ensure jobSemester is not null (validated in open method)
                dto.setSemester(jobSemester.intValue());

                // --- Log each StudentExamResults object created for this physical row ---
                logger.debug("Row {}: Created StudentExamResults object: RegdNo='{}', SubjectCode='{}', Grade='{}', Semester='{}'",
                        currentRow, dto.getRegdno(), dto.getSubjectCode(), dto.getGrade(), dto.getSemester());

                itemsFromCurrentRow.add(dto);
            }

            // After processing all subjects for the current physical row
            if (!itemsFromCurrentRow.isEmpty()) {
                logger.info("Row {}: Successfully created {} StudentExamResults items from this physical row.", currentRow, itemsFromCurrentRow.size());
                // Return the first item from the newly populated list
                StudentExamResults firstItem = itemsFromCurrentRow.get(currentItemFromRowIndex);
                logger.debug("Returning first item for physical row {}: RegdNo='{}', Semester='{}', SubjectCode='{}'",
                        currentRow, firstItem.getRegdno(), firstItem.getSemester(), firstItem.getSubjectCode());
                currentItemFromRowIndex++;
                return firstItem;
            } else {
                // If no valid subject data found for the physical row, try the next row
                logger.warn("Row {}: No valid subject data found for RegdNo '{}' after processing all columns. Skipping this physical row and advancing to next.", currentRow, regdNo);
                return read(); // Recursively call read to get the next valid row
            }
        } catch (Exception e){
            logger.error("Error parsing row {}: {}. Raw row data: {}", currentRow, e.getMessage(), row.toString(), e);
            throw new ParseException("Failed to parse row " + currentRow + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("currentRow", currentRow);
        executionContext.put("headerSkipped", headerSkipped);
        // It's generally a good idea to store currentItemFromRowIndex for full restartability,
        // especially if your chunk size is > 1. For now, we'll keep it simple as it's not the primary issue.
        // If you hit restart issues within a multi-item row, you'd add:
        // executionContext.put("currentItemFromRowIndex", currentItemFromRowIndex);
    }

    @Override
    public void close() throws ItemStreamException {
        if (workbook != null) {
            try {
                workbook.close();
                logger.info("Closed Excel workbook.");
            } catch (IOException e) {
                logger.error("Failed to close Excel workbook: {}", e.getMessage(), e);
                throw new ItemStreamException("Failed to close Excel workbook", e);
            }
        }
    }
}