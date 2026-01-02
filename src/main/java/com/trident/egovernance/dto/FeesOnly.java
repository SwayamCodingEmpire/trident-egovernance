package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.*;

import java.math.BigDecimal;

public record FeesOnly(
        Long feeId,
        BasicFeeBatchDetails batchElements,
        Integer regdYear,
        String description,
        BigDecimal amount,
        String comments,
        TFWType tfwType,
        BigDecimal tatFees,
        BigDecimal tactFfees,
        CfPaymentMode payType
) {
    public FeesOnly(Fees fees) {
        this(
                fees.getFeeId(),
                extractBasicFeeBatchDetails(fees.getBatchId()), // Delegate to helper for batch elements
                fees.getRegdYear(),
                fees.getDescription(),
                fees.getAmount(),
                fees.getComments(),
                fees.getTfwType(),
                fees.getTatFees(),
                fees.getTactFfees(),
                fees.getPayType()
        );
    }
    private static BasicFeeBatchDetails extractBasicFeeBatchDetails(String batchId) {
        // Extract course part
        // Extract course (before digits)
        String courseString = batchId.replaceAll("\\d.*", "");
        Courses course = Courses.valueOf(courseString);

        // Remove course from batchId
        String remaining = batchId.substring(courseString.length());

        // Extract admission year (first 4 digits)
        int admYear = Integer.parseInt(remaining.substring(0, 4));

        // Remaining after year
        String afterYear = remaining.substring(4);

        // Identify student type
        String studentTypeCode = remaining.substring(7, 9);

        StudentType studentType;
        String studentTypeStr;

        if (afterYear.endsWith("REGULAR")) {
            studentType = StudentType.REGULAR;
            studentTypeStr = "REGULAR";
        } else if (afterYear.endsWith("LE")) {
            studentType = StudentType.LE;
            studentTypeStr = "LE";
        } else {
            throw new IllegalArgumentException("Invalid student type in batchId: " + batchId);
        }

        // Extract branch code (between year and student type)
        String branchCode = afterYear.substring(0, afterYear.length() - studentTypeStr.length());

        // Parse college name based on student type
        String collegeCode;
        if (studentType == StudentType.REGULAR) {
            studentType = StudentType.valueOf(remaining.substring(7, 14));
            collegeCode = remaining.substring(14);
        } else {
            collegeCode = remaining.substring(9);
        }

        // Determine college name length (TAT = 3 chars, others = 4)
        CollegeName collegeName;
        if (collegeCode.startsWith("TAT")) {
            collegeName = CollegeName.valueOf(collegeCode.substring(0, 3));
        } else {
            collegeName = CollegeName.valueOf(collegeCode.substring(0, 4));
        }

        return new BasicFeeBatchDetails(admYear, course, branchCode, studentType, collegeName);
    }
//
//    private static BasicFeeBatchDetails extractBasicFeeBatchDetails(String batchId) {
//        // Extract course (before digits)
//        String courseString = batchId.replaceAll("\\d.*", "");
//        Courses course = Courses.valueOf(courseString);
//
//        // Remove course from batchId
//        String remaining = batchId.substring(courseString.length());
//
//        // Extract admission year (first 4 digits)
//        int admYear = Integer.parseInt(remaining.substring(0, 4));
//
//        // Remaining after year
//        String afterYear = remaining.substring(4);
//
//        // Determine studentType from the end
//        StudentType studentType;
//        String studentTypeStr;
//
//        if (afterYear.endsWith("REGULAR")) {
//            studentType = StudentType.REGULAR;
//            studentTypeStr = "REGULAR";
//        } else if (afterYear.endsWith("LE")) {
//            studentType = StudentType.LE;
//            studentTypeStr = "LE";
//        } else {
//            throw new IllegalArgumentException("Invalid student type in batchId: " + batchId);
//        }
//
//        // Extract branch code (between year and student type)
//        String branchCode = afterYear.substring(0, afterYear.length() - studentTypeStr.length());
//
//        return new BasicFeeBatchDetails(admYear, course, branchCode, studentType);
//    }
}
