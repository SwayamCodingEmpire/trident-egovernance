package com.trident.egovernance.global.helpers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class excelToCsvConverter {

    // Helper class to create a MultipartFile from byte array
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] content, String name, String originalFilename, String contentType) {
            this.content = content;
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
        }

        @Override
        public String getName() { return name; }
        @Override
        public String getOriginalFilename() { return originalFilename; }
        @Override
        public String getContentType() { return contentType; }
        @Override
        public boolean isEmpty() { return content == null || content.length == 0; }
        @Override
        public long getSize() { return content.length; }
        @Override
        public byte[] getBytes() throws IOException { return content; }
        @Override
        public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(content); }
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            StreamUtils.copy(content, Files.newOutputStream(dest.toPath()));
        }
    }


    public static MultipartFile convertExcelToCSV(MultipartFile excelFile) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        try (InputStream is = excelFile.getInputStream(); // Correctly get InputStream from MultipartFile
             Workbook workbook = WorkbookFactory.create(is)) { // Use WorkbookFactory to handle both .xls and .xlsx
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    // Basic CSV escaping: Enclose in double quotes if content contains comma or newline
                    String cellValue = getCellValueAsString(cell);
                    if (cellValue.contains(",") || cellValue.contains("\n") || cellValue.contains("\"")) {
                        // Double any existing quotes, then wrap in quotes
                        cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                    }
                    rowData.append(cellValue);
                    rowData.append(",");
                }

                if (rowData.length() > 0) {
                    rowData.setLength(rowData.length() - 1); // Remove trailing comma
                }
                csvContent.append(rowData).append("\n");
            }

        } catch (IOException e) {
            System.err.println("Error during Excel to CSV conversion: " + e.getMessage());
            throw e; // Re-throw the exception for proper error handling upstream
        }

        // Return a new MultipartFile containing the generated CSV content
        String csvFileName = excelFile.getOriginalFilename().replaceAll("\\.(xlsx|xls)$", ".csv");
        return new ByteArrayMultipartFile(
                csvContent.toString().getBytes(StandardCharsets.UTF_8), // Convert String to bytes
                "csvFile", // Name for the MultipartFile
                csvFileName,
                "text/csv" // Content type for CSV
        );
    }

    // Helper method to get cell value as String, handling different cell types
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // You might want to format the date more specifically (e.g., "yyyy-MM-dd")
                    yield cell.getDateCellValue().toInstant().toString(); // Example, adjust format as needed
                } else {
                    // Format numeric values to avoid scientific notation for large numbers
                    // or keep trailing .0 for integers if that's preferred.
                    DataFormatter formatter = new DataFormatter();
                    yield formatter.formatCellValue(cell);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Evaluate formula to get its cached value
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                yield switch (cellValue.getCellType()) {
                    case NUMERIC -> String.valueOf(cellValue.getNumberValue());
                    case STRING -> cellValue.getStringValue();
                    case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
                    case ERROR -> FormulaError.forInt(cellValue.getErrorValue()).getString();
                    default -> "";
                };
            }
            case BLANK -> "";
            default -> "";
        };
    }
}
