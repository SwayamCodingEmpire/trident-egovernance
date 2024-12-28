package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MoneyReceiptPDFGenerator {
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 15;
    private PDDocument document;
    private PDPage page;
    private float currentY;
    private float pageWidth;
    private float pageHeight;

    public byte[] generatePDF(StudentBasicDTO studentDetails, MoneyReceipt receipt) throws IOException {
        document = new PDDocument();
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        pageWidth = page.getMediaBox().getWidth();
        pageHeight = page.getMediaBox().getHeight();
        currentY = pageHeight - MARGIN;


        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Add background watermark
            addBackgroundWatermark(contentStream);

            // Add header with logo and institute details
            addHeader(contentStream);

            // Add Money Receipt title
            addTitle(contentStream);

            // Add MR Number and Date
            addHeaderDetails(contentStream, receipt.getMrNo(), receipt.getPaymentDate());

            // Add Personal Details Table
            addPersonalDetailsTable(contentStream, studentDetails);

            // Add TAT Fee Details
            if (receipt.getTat() != null && !receipt.getTat().isEmpty()) {
                addTatTable(contentStream, receipt.getTat(), receipt.getTatTotalAmount());
            }

            // Add TACTF Fee Details
            if (receipt.getTactF() != null && !receipt.getTactF().isEmpty()) {
                addTactfTable(contentStream, receipt.getTactF(), receipt.getTactFTotalAmount());
            }

            // Add Payment Collection Details
            addPaymentCollectionDetails(contentStream, receipt.getFeeCollectionDetails());

            // Add Payment Dues Details if present
            if (receipt.getPaymentDuesDetails() != null) {
                addPaymentDuesDetails(contentStream, receipt.getPaymentDuesDetails());
            }

            // Add Footer
            addFooter(contentStream);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        return baos.toByteArray();
    }

    private void addBackgroundWatermark(PDPageContentStream contentStream) throws IOException {
        // Load and draw the watermark image with transparency
        PDImageXObject image = PDImageXObject.createFromFile("path/to/tat-logo.jpg", document);
        float scale = 0.5f;
        float imageWidth = image.getWidth() * scale;
        float imageHeight = image.getHeight() * scale;
        float x = (pageWidth - imageWidth) / 2;
        float y = (pageHeight - imageHeight) / 2;

        contentStream.saveGraphicsState();
        contentStream.setNonStrokingColor(new Color(1f, 1f, 1f, 0.1f)); // 10% opacity
        contentStream.drawImage(image, x, y, imageWidth, imageHeight);
        contentStream.restoreGraphicsState();
    }

    private void addHeaderDetails(PDPageContentStream contentStream, Long mrNo, String paymentDate) throws IOException {
        // MR Number on left
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText("MRNO: " + mrNo);
        contentStream.endText();

        // Date on right
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        float dateX = pageWidth - MARGIN - 150; // Adjust based on your needs
        contentStream.newLineAtOffset(dateX, currentY);
        contentStream.showText("DATE: " + paymentDate);
        contentStream.endText();

        currentY -= LINE_HEIGHT * 2; // Move down after header details
    }
    private void addTitle(PDPageContentStream contentStream) throws IOException {
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.addRect(MARGIN + 150, currentY - 25, 150, 25);
        contentStream.fill();

        contentStream.setNonStrokingColor(new Color(255, 215, 0)); // Gold color
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        contentStream.newLineAtOffset(MARGIN + 170, currentY - 20);
        contentStream.showText("MONEY RECEIPT");
        contentStream.endText();

        contentStream.setNonStrokingColor(Color.BLACK); // Reset color
        currentY -= 40;
    }

    private void addHeader(PDPageContentStream contentStream) throws IOException {
        // Add logo
        PDImageXObject logo = PDImageXObject.createFromFile("path/to/tat-logo.jpg", document);
        contentStream.drawImage(logo, MARGIN, currentY - 80, 100, 80);

        // Add institute details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        contentStream.newLineAtOffset(MARGIN + 120, currentY - 30);
        contentStream.showText("TRIDENT ACADEMY OF TECHNOLOGY");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(MARGIN + 120, currentY - 45);
        contentStream.showText("F2/A, Chandaka Industrial Estate, Bhubaneswar - 751024., Odisha");
        contentStream.endText();

        currentY -= 100;
    }

    private void addPersonalDetailsTable(PDPageContentStream contentStream, StudentBasicDTO student)
            throws IOException {
        String[][] data = {
                {"NAME:", student.studentName()},
                {"REGDNO:", student.regdNo()},
                {"BRANCH:", student.branchCode()},
                {"ADMISSION YEAR:", student.admissionYear()},
                {"CURRENT YEAR:", String.valueOf(student.currentYear())}
        };

        float tableWidth = 300;
        float rowHeight = 20;
        float tableHeight = rowHeight * data.length;

        // Draw table borders
        contentStream.setLineWidth(1f);
        contentStream.addRect(MARGIN, currentY - tableHeight, tableWidth, tableHeight);
        contentStream.stroke();

        float textY = currentY - rowHeight;

        for (String[] row : data) {
            // Draw row separator
            contentStream.moveTo(MARGIN, textY + rowHeight - 20);
            contentStream.lineTo(MARGIN + tableWidth, textY + rowHeight - 20);
            contentStream.stroke();

            // Add text
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            contentStream.newLineAtOffset(MARGIN + 5, textY);
            contentStream.showText(row[0]);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.newLineAtOffset(MARGIN + 100, textY);
            contentStream.showText(row[1]);
            contentStream.endText();

            textY -= rowHeight;
        }

        currentY = textY - 20;
    }

    private void addPaymentCollectionDetails(PDPageContentStream contentStream,
                                             FeeCollectionDetails details) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText("Received as per following details:");
        contentStream.endText();
        currentY -= LINE_HEIGHT * 2;

        float[] columnWidths = {100, 100, 100, 150};
        String[] headers = {"PAYMENT MODE", "DD NO", "DD DATE", "DD BANK"};

        drawTableHeader(contentStream, columnWidths, headers, currentY);
        currentY -= LINE_HEIGHT;

        String[] rowData = {
                details.paymentMode().name(),
                details.ddNo() != null ? details.ddNo() : "",
                details.ddDate() != null ? details.ddDate() : "",
                details.ddBank() != null ? details.ddBank() : ""
        };

        drawTableRow(contentStream, columnWidths, rowData, currentY);
        currentY -= LINE_HEIGHT * 2;
    }

    private void addPaymentDuesDetails(PDPageContentStream contentStream,
                                       PaymentDuesDetails dues) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText("Details of Dues:");
        contentStream.endText();
        currentY -= LINE_HEIGHT * 2;

        float[] columnWidths = {100, 100, 100, 100};
        String[] headers = {"Arrears", "Current Dues", "Total Paid", "Amount Due"};

        drawTableHeader(contentStream, columnWidths, headers, currentY);
        currentY -= LINE_HEIGHT;

        String[] rowData = {
                String.format("%.2f", dues.arrears()),
                String.format("%.2f", dues.currentDues()),
                String.format("%.2f", dues.totalPaid()),
                String.format("%.2f", dues.amountDue())
        };

        drawTableRow(contentStream, columnWidths, rowData, currentY);
        currentY -= LINE_HEIGHT * 2;
    }

    private void drawTableHeader(PDPageContentStream contentStream, float[] columnWidths,
                                 String[] headers, float y) throws IOException {
        float xOffset = MARGIN;

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.beginText();

        for (int i = 0; i < headers.length; i++) {
            contentStream.newLineAtOffset(xOffset, y);
            contentStream.showText(headers[i]);
            xOffset += columnWidths[i];
        }

        contentStream.endText();

        // Draw lines
        contentStream.setLineWidth(1f);
        contentStream.moveTo(MARGIN, y - 5);
        contentStream.lineTo(MARGIN + sum(columnWidths), y - 5);
        contentStream.stroke();
    }

    private void drawTableRow(PDPageContentStream contentStream, float[] columnWidths,
                              String[] rowData, float y) throws IOException {
        float xOffset = MARGIN;

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.beginText();

        for (int i = 0; i < rowData.length; i++) {
            contentStream.newLineAtOffset(xOffset, y);
            contentStream.showText(rowData[i]);
            xOffset += columnWidths[i];
        }

        contentStream.endText();
    }


    private void addTatTable(PDPageContentStream contentStream, List<MrDetailsDto> tatDetails,
                             MoneyDTO totalAmount) throws IOException {
        // Table header
        float[] columnWidths = {50, 300, 100};
        String[] headers = {"SL NO", "PARTICULARS", "AMOUNT"};

        drawTableHeader(contentStream, columnWidths, headers, currentY);
        currentY -= LINE_HEIGHT;

        // Table rows
        for (int i = 0; i < tatDetails.size(); i++) {
            MrDetailsDto detail = tatDetails.get(i);
            String[] rowData = {
                    String.valueOf(i + 1),
                    detail.getParticulars(),
                    String.format("%.2f", detail.getAmount())
            };
            drawTableRow(contentStream, columnWidths, rowData, currentY);
            currentY -= LINE_HEIGHT;
        }

        // Total amount
        drawTotal(contentStream, totalAmount.amount(), totalAmount.amountInWords());
        currentY -= LINE_HEIGHT * 2;
    }

    private void addTactfTable(PDPageContentStream contentStream, List<MrDetailsDto> tactfDetails,
                               MoneyDTO totalAmount) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText("Collection on behalf of Service Provider");
        contentStream.endText();
        currentY -= LINE_HEIGHT * 2;

        // Use same table structure as TAT table
        float[] columnWidths = {50, 300, 100};
        String[] headers = {"SL NO", "PARTICULARS", "AMOUNT"};

        drawTableHeader(contentStream, columnWidths, headers, currentY);
        currentY -= LINE_HEIGHT;

        for (int i = 0; i < tactfDetails.size(); i++) {
            MrDetailsDto detail = tactfDetails.get(i);
            String[] rowData = {
                    String.valueOf(i + 1),
                    detail.getParticulars(),
                    String.format("%.2f", detail.getAmount())
            };
            drawTableRow(contentStream, columnWidths, rowData, currentY);
            currentY -= LINE_HEIGHT;
        }

        drawTotal(contentStream, totalAmount.amount(), totalAmount.amountInWords());
        currentY -= LINE_HEIGHT * 2;
    }
        private void drawTotal(PDPageContentStream contentStream, BigDecimal amount, String amountInWords)
            throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText(String.format("Total: Rs. %.2f", amount));
        contentStream.newLineAtOffset(0, -LINE_HEIGHT);
        contentStream.showText("(" + amountInWords + ")");
        contentStream.endText();
    }

    private float sum(float[] array) {
        float sum = 0;
        for (float value : array) {
            sum += value;
        }
        return sum;
    }

    private void addFooter(PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
        contentStream.newLineAtOffset(MARGIN, MARGIN);
        contentStream.showText("This is a computer generated receipt");
        contentStream.endText();
    }
}


// Helper methods remain the same as in the previous version
    // ... (rest of the helper methods)
