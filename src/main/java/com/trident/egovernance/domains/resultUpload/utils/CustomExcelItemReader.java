package com.trident.egovernance.domains.resultUpload.utils;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("^R[A-Z0-9]{4}\\d{3}$"); // From your original code

    private int rowNoCol = -1;
    private Map<Integer, String> subjectColumnIndexToCodeMap = new HashMap<>();

    public CustomExcelItemReader(){
        this.dataFormatter = new DataFormatter();
    }
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if(resource == null){
            throw new ItemStreamException("Excel file resource must be set.");
        }
        try{
            InputStream inputStream = resource.getInputStream();
            this.workbook = WorkbookFactory.create(inputStream);
            this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator(); // Initialize evaluator
            this.sheet = workbook.getSheetAt(0);
            this.rowIterator = sheet.iterator();

            if(executionContext.containsKey("currentRow")){
                currentRow = executionContext.getInt("currentRow");
                for(int i=0; i < currentRow && rowIterator.hasNext(); i++){
                    rowIterator.next();
                }
                headerSkipped = false;
            }

            if(!headerSkipped && rowIterator.hasNext()){
                Row headerRow = rowIterator.next();
                rowIterator.next();
                currentRow++;
                headerSkipped = true;

                for(Cell cell : headerRow){
                    String header = dataFormatter.formatCellValue(cell).trim();

                    if("Roll No".equals(header)){
                        rowNoCol = cell.getColumnIndex();
                    }
                }
            }
        } catch (IOException e){
            throw new ItemStreamException("Failed to open Excel file" + resource.getFilename(), e);
        } catch (Exception e){
            throw new ItemStreamException("Error initializing Excel reader: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentExamResults read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(rowIterator != null && rowIterator.hasNext()){
            Row row = rowIterator.next();
            currentRow++;

            if(row == null || row.getCell(0) == null || dataFormatter.formatCellValue(row.getCell(0)).trim().isEmpty()){
                logger.debug("Skipping empty or malformed row at index {}.", currentRow);
                return read();
            }

            StudentExamResults dto = new StudentExamResults();
            try{
                dto.setRegdno(dataFormatter.formatCellValue(row.getCell(0)).trim());
                if(dto.getRegdno().isEmpty()){
                    logger.warn("Row {}: RegdNo is empty. Skipping this record.", currentRow);
                    return read();
                }

                dto.setSemester(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)).trim()));
                if(dto.getSemester() < 0 || dto.getSemester() > 9){
                    logger.warn("Row {}: Semester is invalid. Skipping this record.", currentRow);
                }

                dto.setSubjectCode(dataFormatter.formatCellValue(row.getCell(2)).trim());
                if(dto.getSubjectCode().isEmpty() || !SUBJECT_CODE_PATTERN.matcher(dto.getSubjectCode()).matches()){
                    logger.warn("Row {}: Invalid or empty SubjectCode '{}'. Skipping this record.", currentRow, dto.getSubjectCode());
                    return read();
                }

                String rawGrade = dataFormatter.formatCellValue(row.getCell(3), formulaEvaluator).trim();
                if("F(Ex)".equalsIgnoreCase(rawGrade) || "F(Int)".equalsIgnoreCase(rawGrade)){
                    dto.setGrade("F");
                    logger.debug("Row {}: Converted grade '{}' to 'F' for Regdno: {}, Subject: {}", currentRow, rawGrade, dto.getRegdno(), dto.getSubjectCode());
                } else if(rawGrade.isEmpty() || "-".equals(rawGrade) || "NA".equalsIgnoreCase(rawGrade)){
                    logger.debug("Row {}: Skipping grade '{}' for subject '{}' (RegdNo: {}) as it's empty or special.", currentRow, rawGrade, dto.getRegdno(), dto.getSubjectCode());
                    return read();
                } else {
                    dto.setGrade(rawGrade);
                }

                dto.setCredits(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(4)).trim()));

                dto.setResultPublishDate(dataFormatter.formatCellValue(row.getCell(5)).trim());
            } catch (Exception e){
                logger.error("Error parsing row {}: {}. Row data: {}", currentRow, e.getMessage(), row);
                throw new ParseException("Failed to parse row " + currentRow + ": " + e.getMessage(), e);
            }
            return dto;
        }
        return null;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("currentRow", currentRow);
        executionContext.put("headerSkipped", headerSkipped);
    }

    @Override
    public void close() throws ItemStreamException {
        if (workbook != null) {
            try{
                workbook.close();
            } catch (IOException e) {
                logger.error("Failed to close Excel workbook: {}", e.getMessage(), e);
                throw new ItemStreamException("Failed to close Excel workbook", e);
            }
        }
    }
}
