package com.trident.egovernance.global.services;


public class PDFGenerationReceiptService {
////    private static final String LOGO_PATH = "static/images/tat-logo.jpg";
////    private static final float POINTS_PER_MM = 2.83465f;
////    private static final float MARGIN = 20 * POINTS_PER_MM;
////
////    public byte[] generateMoneyReceipt(MoneyReceipt receipt) {
////        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
////             PDDocument document = new PDDocument()) {
////
////            PDPage page = new PDPage(PDRectangle.A4);
////            document.addPage(page);
////
////            PDPageContentStream contentStream = new PDPageContentStream(document, page);
////
////            float pageWidth = page.getMediaBox().getWidth();
////            float pageHeight = page.getMediaBox().getHeight();
////            float workingWidth = pageWidth - (2 * MARGIN);
////
////            // Add watermark
////            addWatermark(document, page);
////
////            // Start writing content
////            contentStream.beginText();
////
////            float yPosition = pageHeight - MARGIN;
////
////            // Add header with logo and institution details
////            yPosition = addHeader(document, contentStream, workingWidth, yPosition);
////
////            // Add title
////            yPosition = addTitle(contentStream, workingWidth, yPosition);
////
////            // Add MR details (number and date)
////            yPosition = addMRDetails(contentStream, workingWidth, yPosition, receipt);
////
////            // Add TAT details if present
////            if (receipt.getTat() != null && !receipt.getTat().isEmpty()) {
////                yPosition = addTATDetails(contentStream, workingWidth, yPosition, receipt);
////            }
////
////            // Add TACTF details if present
////            if (receipt.getTactF() != null && !receipt.getTactF().isEmpty()) {
////                yPosition = addTACTFDetails(contentStream, workingWidth, yPosition, receipt);
////            }
////
////            // Add payment collection details
////            if (receipt.getFeeCollectionDetails() != null) {
////                yPosition = addPaymentDetails(contentStream, workingWidth, yPosition,
////                        receipt.getFeeCollectionDetails());
////            }
////
////            // Add payment dues if present
////            if (receipt.getPaymentDuesDetails() != null) {
////                yPosition = addPaymentDuesDetails(contentStream, workingWidth, yPosition,
////                        receipt.getPaymentDuesDetails());
////            }
////
////            // Add footer
////            addFooter(contentStream, workingWidth, yPosition);
////
////            contentStream.endText();
////            contentStream.close();
////
////            document.save(baos);
////            return baos.toByteArray();
////
////        } catch (Exception e) {
////            log.error("Error generating PDF: ", e);
////            throw new RuntimeException("Failed to generate PDF", e);
////        }
////    }
////
////    private float addMRDetails(PDPageContentStream contentStream, float width, float yPosition,
////                               MoneyReceipt receipt) throws IOException {
////        PDType1Font font = PDType1Font.HELVETICA_BOLD;
////        contentStream.setFont(font, 12);
////
////        // MR Number
////        String mrNo = "MR No: " + receipt.getMrNo();
////        contentStream.newLineAtOffset(MARGIN, yPosition);
////        contentStream.showText(mrNo);
////
////        // Payment Date
////        String date = "Date: " + receipt.getPaymentDate();
////        float dateWidth = font.getStringWidth(date) / 1000 * 12;
////        contentStream.newLineAtOffset(width - dateWidth - MARGIN, 0);
////        contentStream.showText(date);
////
////        return yPosition - 30;
////    }
////
////    private float addTATDetails(PDPageContentStream contentStream, float width, float yPosition,
////                                MoneyReceipt receipt) throws IOException {
////        // Table headers
////        String[] headers = {"Sl No", "Particulars", "Amount"};
////        float[] columnWidths = {50f, 350f, 100f};
////
////        // Prepare table data
////        List<String[]> rows = receipt.getTat().stream()
////                .map(tat -> new String[]{
////                        String.valueOf(tat.getSlNo()),
////                        tat.getParticulars(),
////                        tat.getAmount().setScale(2, RoundingMode.HALF_UP).toString()
////                })
////                .collect(Collectors.toList());
////
////        // Draw table
////        drawTable(contentStream, yPosition, MARGIN, headers, rows, columnWidths);
////
////        // Add total if present
////        if (receipt.getTatTotalAmount() != null) {
////            float totalY = yPosition - ((rows.size() + 1) * 20f) - 10f;
////            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
////            contentStream.newLineAtOffset(MARGIN + 350f, totalY);
////            contentStream.showText("Total: " +
////                    receipt.getTatTotalAmount().amount().setScale(2, RoundingMode.HALF_UP));
////
////            // Amount in words
////            contentStream.newLineAtOffset(0, -20);
////            contentStream.showText("(" + receipt.getTatTotalAmount().amountInWords() + ")");
////
////            return totalY - 40;
////        }
////
////        return yPosition - ((rows.size() + 1) * 20f) - 20f;
////    }
////
////    private float addTACTFDetails(PDPageContentStream contentStream, float width, float yPosition,
////                                  MoneyReceipt receipt) throws IOException {
////        // Similar to TAT details but for TACTF
////        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
////        contentStream.newLineAtOffset(MARGIN, yPosition);
////        contentStream.showText("Collection on behalf of Service Provider");
////
////        String[] headers = {"Sl No", "Particulars", "Amount"};
////        float[] columnWidths = {50f, 350f, 100f};
////
////        List<String[]> rows = receipt.getTactF().stream()
////                .map(tactf -> new String[]{
////                        String.valueOf(tactf.getSlNo()),
////                        tactf.getParticulars(),
////                        tactf.getAmount().setScale(2, RoundingMode.HALF_UP).toString()
////                })
////                .collect(Collectors.toList());
////
////        drawTable(contentStream, yPosition - 20f, MARGIN, headers, rows, columnWidths);
////
////        if (receipt.getTactFTotalAmount() != null) {
////            float totalY = yPosition - ((rows.size() + 1) * 20f) - 30f;
////            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
////            contentStream.newLineAtOffset(350f, totalY);
////            contentStream.showText("Total: " +
////                    receipt.getTactFTotalAmount().amount().setScale(2, RoundingMode.HALF_UP));
////
////            contentStream.newLineAtOffset(0, -20);
////            contentStream.showText("(" + receipt.getTactFTotalAmount().amountInWords() + ")");
////
////            return totalY - 40;
////        }
////
////        return yPosition - ((rows.size() + 1) * 20f) - 40f;
////    }
////    private void addWatermark(PDDocument document, PDPage page) throws IOException {
////        // Load logo image
////        BufferedImage logoImage = ImageIO.read(getClass().getClassLoader().getResource(LOGO_PATH));
////        PDImageXObject logo = LosslessFactory.createFromImage(document, logoImage);
////
////        // Calculate center position and size
////        float pageWidth = page.getMediaBox().getWidth();
////        float pageHeight = page.getMediaBox().getHeight();
////        float logoWidth = pageWidth * 0.5f; // 50% of page width
////        float logoHeight = logoWidth * (logo.getHeight() / (float) logo.getWidth());
////        float x = (pageWidth - logoWidth) / 2;
////        float y = (pageHeight - logoHeight) / 2;
////
////        // Create transparent watermark
////        PDPageContentStream watermark = new PDPageContentStream(document, page,
////                PDPageContentStream.AppendMode.PREPEND, true, true);
////        watermark.setGraphicsStateParameters(new PDExtendedGraphicsState());
////        watermark.setNonStrokingColor(Color.GRAY);
////        watermark.setGraphicsStateParameters(new PDExtendedGraphicsState());
////        watermark.getGraphicsStateParameters().setNonStrokingAlphaConstant(0.1f);
////        watermark.drawImage(logo, x, y, logoWidth, logoHeight);
////        watermark.close();
////    }
////
////    private float addHeader(PDDocument document, PDPageContentStream contentStream,
////                            float width, float yPosition) throws IOException {
////        // Load logo
////        BufferedImage logoImage = ImageIO.read(getClass().getClassLoader().getResource(LOGO_PATH));
////        PDImageXObject logo = LosslessFactory.createFromImage(document, logoImage);
////
////        // Add logo
////        float logoWidth = 100;
////        float logoHeight = 89;
////        contentStream.drawImage(logo, MARGIN, yPosition - logoHeight, logoWidth, logoHeight);
////
////        // Add institution details
////        PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
////        PDType1Font addressFont = PDType1Font.HELVETICA;
////
////        contentStream.setFont(titleFont, 16);
////        String institutionName = "TRIDENT ACADEMY OF TECHNOLOGY";
////        float titleWidth = titleFont.getStringWidth(institutionName) / 1000 * 16;
////        contentStream.newLineAtOffset((width - titleWidth) / 2 + MARGIN, yPosition - 20);
////        contentStream.showText(institutionName);
////
////        contentStream.setFont(addressFont, 10);
////        String address = "F2/A, Chandaka Industrial Estate, Bhubaneswar - 751024., Odisha";
////        float addressWidth = addressFont.getStringWidth(address) / 1000 * 10;
////        contentStream.newLineAtOffset(0, -15);
////        contentStream.showText(address);
////
////        String contact = "Tel.: 0674-6649037, 6649038    Fax.:0674-6649043";
////        float contactWidth = addressFont.getStringWidth(contact) / 1000 * 10;
////        contentStream.newLineAtOffset(0, -12);
////        contentStream.showText(contact);
////
////        return yPosition - logoHeight - 40;
////    }
////
////    private float addTitle(PDPageContentStream contentStream, float width, float yPosition)
////            throws IOException {
////        PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
////        contentStream.setFont(titleFont, 14);
////        contentStream.setNonStrokingColor(Color.ORANGE);
////
////        String title = "MONEY RECEIPT";
////        float titleWidth = titleFont.getStringWidth(title) / 1000 * 14;
////
////        // Add black background
////        contentStream.setNonStrokingColor(Color.BLACK);
////        contentStream.addRect((width - titleWidth) / 2 - 10 + MARGIN, yPosition - 25,
////                titleWidth + 20, 30);
////        contentStream.fill();
////
////        // Add text
////        contentStream.setNonStrokingColor(Color.ORANGE);
////        contentStream.newLineAtOffset((width - titleWidth) / 2 + MARGIN, yPosition - 20);
////        contentStream.showText(title);
////
////        return yPosition - 60;
////    }
////
////    // ... Additional methods for other sections ...
////
////    private void drawTable(PDPageContentStream contentStream, float y, float margin,
////                           String[] headers, List<String[]> rows, float[] columnWidths)
////            throws IOException {
////        final float rowHeight = 20f;
////        final float tableWidth = Arrays.stream(columnWidths).sum();
////        float nextY = y;
////
////        // Draw header row
////        PDType1Font headerFont = PDType1Font.HELVETICA_BOLD;
////        contentStream.setFont(headerFont, 10);
////        float nextX = margin;
////        for (int i = 0; i < headers.length; i++) {
////            contentStream.addRect(nextX, nextY, columnWidths[i], rowHeight);
////            contentStream.beginText();
////            contentStream.newLineAtOffset(nextX + 5, nextY + 5);
////            contentStream.showText(headers[i]);
////            contentStream.endText();
////            nextX += columnWidths[i];
////        }
////        nextY -= rowHeight;
////
////        // Draw rows
////        PDType1Font rowFont = PDType1Font.HELVETICA;
////        contentStream.setFont(rowFont, 10);
////        for (String[] row : rows) {
////            nextX = margin;
////            for (int i = 0; i < row.length; i++) {
////                contentStream.addRect(nextX, nextY, columnWidths[i], rowHeight);
////                contentStream.beginText();
////                contentStream.newLineAtOffset(nextX + 5, nextY + 5);
////                contentStream.showText(row[i]);
////                contentStream.endText();
////                nextX += columnWidths[i];
////            }
////            nextY -= rowHeight;
////        }
////
////        // Draw all table borders
////        contentStream.stroke();
////    }
//private static final String COLLEGE_HEADER = "TRIDENT ACADEMY OF TECHNOLOGY";
//    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
//    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
//    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);
//
//    public byte[] generatePdf(PDFObject pdfObject) throws DocumentException, IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Document document = new Document(PageSize.A4);
//        PdfWriter.getInstance(document, baos);
//
//        document.open();
//
//        // Add header
//        addHeader(document);
//
//        // Add student details
//        addStudentDetails(document, pdfObject.personalDetails(), pdfObject.mrDetails());
//
//        // Add TAT fees table
//        addFeesTable(document, "College Fees", pdfObject.mrDetails().getTat(),
//                pdfObject.mrDetails().getTatTotalAmount());
//
//        // Add signature line
//        addSignatureLine(document, "Receiving\nOfficer\nFor TAT");
//
//        // Add service provider section
//        Paragraph serviceHeader = new Paragraph("Collection on behalf of Service Provider", HEADER_FONT);
//        serviceHeader.setSpacingBefore(20);
//        document.add(serviceHeader);
//
//        // Add service provider fees table
//        addFeesTable(document, "Service Provider Fees", pdfObject.mrDetails().getTactF(),
//                pdfObject.mrDetails().getTactFTotalAmount());
//
//        // Add service provider signature
//        addSignatureLine(document, "Receiving Officer\nFor Service Provider through TAT");
//
//        // Add payment details
//        addPaymentDetails(document, pdfObject.mrDetails().getFeeCollectionDetails());
//
//        // Add dues details
//        addDuesDetails(document, pdfObject.mrDetails().getPaymentDuesDetails());
//
//        // Add footer
//        addFooter(document);
//
//        document.close();
//        return baos.toByteArray();
//    }
//
//    private void addHeader(Document document) throws DocumentException {
//        Paragraph header = new Paragraph(COLLEGE_HEADER, HEADER_FONT);
//        header.setAlignment(Element.ALIGN_CENTER);
//        document.add(header);
//
//        Paragraph address = new Paragraph(
//                "F2/A, Chandaka Industrial Estate, Bhubaneswar - 751024., Odisha", NORMAL_FONT);
//        address.setAlignment(Element.ALIGN_CENTER);
//        document.add(address);
//
//        Paragraph contact = new Paragraph(
//                "Tel.: 0674-6649037, 6649038 Fax.:0674-6649043", NORMAL_FONT);
//        contact.setAlignment(Element.ALIGN_CENTER);
//        document.add(contact);
//
//        Paragraph title = new Paragraph("MONEY RECEIPT", HEADER_FONT);
//        title.setAlignment(Element.ALIGN_CENTER);
//        title.setSpacingBefore(20);
//        document.add(title);
//    }
//
//    private void addStudentDetails(Document document, StudentBasicDTO student,
//                                   MoneyReceipt receipt) throws DocumentException {
//        PdfPTable table = new PdfPTable(2);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(20);
//
//        addTableCell(table, "MR NO: " + receipt.getMrNo());
//        addTableCell(table, "DATE: " + receipt.getPaymentDate());
//        addTableCell(table, "NAME: " + student.studentName());
//        addTableCell(table, "REGD NO: " + student.regdNo());
//        addTableCell(table, "BRANCH: " + student.branchCode());
//        addTableCell(table, "ADMISSION YEAR: " + student.admissionYear());
//
//        document.add(table);
//    }
//
//    private void addFeesTable(Document document, String title, List<MrDetailsDto> fees,
//                              MoneyDTO totalAmount) throws DocumentException {
//        PdfPTable table = new PdfPTable(3);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(20);
//
//        // Headers
//        addTableHeader(table, "SL NO");
//        addTableHeader(table, "PARTICULARS");
//        addTableHeader(table, "AMOUNT");
//
//        // Content
//        for (int i = 0; i < fees.size(); i++) {
//            MrDetailsDto fee = fees.get(i);
//            addTableCell(table, String.valueOf(i + 1));
//            addTableCell(table, fee.getParticular());
//            addTableCell(table, String.format("%.2f", fee.getAmount()));
//        }
//
//        // Total
//        addTableCell(table, "");
//        addTableCell(table, "TOTAL:");
//        addTableCell(table, String.format("%.2f", totalAmount.getAmount()));
//
//        document.add(table);
//
//        // Amount in words
//        Paragraph amountInWords = new Paragraph(
//                String.format("(Rupees %s Only)",
//                        NumberToWords.convert(totalAmount.getAmount().longValue())),
//                NORMAL_FONT
//        );
//        amountInWords.setSpacingBefore(10);
//        document.add(amountInWords);
//    }
//
//    private void addPaymentDetails(Document document, FeeCollectionDetails details)
//            throws DocumentException {
//        Paragraph title = new Paragraph("Received as per following details:", NORMAL_FONT);
//        title.setSpacingBefore(20);
//        document.add(title);
//
//        PdfPTable table = new PdfPTable(4);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10);
//
//        addTableHeader(table, "PAYMENT MODE");
//        addTableHeader(table, "DD NO");
//        addTableHeader(table, "DD DATE");
//        addTableHeader(table, "DD BANK");
//
//        addTableCell(table, details.getPaymentMode().toString());
//        addTableCell(table, details.getDdNo() != null ? details.getDdNo() : "");
//        addTableCell(table, details.getDdDate() != null ? details.getDdDate() : "");
//        addTableCell(table, details.getDdBank() != null ? details.getDdBank() : "");
//
//        document.add(table);
//    }
//
//    private void addDuesDetails(Document document, PaymentDuesDetails dues)
//            throws DocumentException {
//        Paragraph title = new Paragraph("Details of Dues:", NORMAL_FONT);
//        title.setSpacingBefore(20);
//        document.add(title);
//
//        PdfPTable table = new PdfPTable(4);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10);
//
//        addTableHeader(table, "Arrears");
//        addTableHeader(table, "Current Dues");
//        addTableHeader(table, "Total Paid");
//        addTableHeader(table, "Amount Due");
//
//        addTableCell(table, String.format("%.2f", dues.getArrears()));
//        addTableCell(table, String.format("%.2f", dues.getCurrentDues()));
//        addTableCell(table, String.format("%.2f", dues.getTotalPaid()));
//        addTableCell(table, String.format("%.2f", dues.getAmountDue()));
//
//        document.add(table);
//    }
//
//    private void addSignatureLine(Document document, String text) throws DocumentException {
//        Paragraph signature = new Paragraph(text, NORMAL_FONT);
//        signature.setAlignment(Element.ALIGN_RIGHT);
//        signature.setSpacingBefore(30);
//        document.add(signature);
//    }
//
//    private void addFooter(Document document) throws DocumentException {
//        Paragraph footer = new Paragraph();
//        footer.setSpacingBefore(30);
//        footer.add(new Chunk("Cheques/DDs are subject to realisation.\n", SMALL_FONT));
//        footer.add(new Chunk("Please Check the Money Receipt before Leaving the Counter\n",
//                SMALL_FONT));
//        footer.add(new Chunk("This is a computer generated receipt", SMALL_FONT));
//        document.add(footer);
//    }
//
//    private void addTableCell(PdfPTable table, String text) {
//        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
//        cell.setBorder(Rectangle.NO_BORDER);
//        cell.setPadding(5);
//        table.addCell(cell);
//    }
//
//    private void addTableHeader(PdfPTable table, String text) {
//        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 10, Font.BOLD)));
//        cell.setBackgroundColor(new Color(230, 230, 230));
//        cell.setPadding(5);
//        table.addCell(cell);
//    }
}
