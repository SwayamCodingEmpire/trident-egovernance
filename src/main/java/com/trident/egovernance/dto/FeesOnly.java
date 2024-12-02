package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.helpers.CfPaymentMode;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.helpers.TFWType;

import java.math.BigDecimal;

public record FeesOnly(
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
        // Extract the course part
        String courseString = batchId.replaceAll("\\d.*", ""); // Get part before digits
        Courses course = Courses.valueOf(courseString); // Map to enum

        // Extract the rest of batchId
        String yearBranchStudent = batchId.replace(courseString, ""); // Remove course part
        int admYear = Integer.parseInt(yearBranchStudent.substring(0, 4)); // Admission year
        String branchCode = yearBranchStudent.substring(4, 7); // Branch code
        StudentType studentType = StudentType.valueOf(yearBranchStudent.substring(7)); // Student type

        // Construct BasicFeeBatchDetails
        return new BasicFeeBatchDetails(admYear, course, branchCode, studentType);
    }
}
