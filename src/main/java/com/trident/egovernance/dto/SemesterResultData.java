package com.trident.egovernance.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record SemesterResultData(
        List<SemesterResultAnalysis> semesterResultAnalyses
) {
    public SemesterResultData(
            SGPADTO maxSGPAs,
            SGPADTO avgSGPAs,
            SGPADTO studentSGPAs
    ) {
        this(createSemesterResultAnalyses(maxSGPAs, avgSGPAs, studentSGPAs));
    }

    private static List<SemesterResultAnalysis> createSemesterResultAnalyses(
            SGPADTO maxSGPAs,
            SGPADTO avgSGPAs,
            SGPADTO studentSGPAs
    ) {
        List<SemesterResultAnalysis> analyses = new ArrayList<>();

        // Manually access record components
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa1(), avgSGPAs.sgpa1(), studentSGPAs.sgpa1()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa2(), avgSGPAs.sgpa2(), studentSGPAs.sgpa2()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa3(), avgSGPAs.sgpa3(), studentSGPAs.sgpa3()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa4(), avgSGPAs.sgpa4(), studentSGPAs.sgpa4()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa5(), avgSGPAs.sgpa5(), studentSGPAs.sgpa5()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa6(), avgSGPAs.sgpa6(), studentSGPAs.sgpa6()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa7(), avgSGPAs.sgpa7(), studentSGPAs.sgpa7()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.sgpa8(), avgSGPAs.sgpa8(), studentSGPAs.sgpa8()
        ));
        analyses.add(createAnalysisIfNotZero(
                maxSGPAs.cgpa(), avgSGPAs.cgpa(), studentSGPAs.cgpa()
        ));

        // Remove null entries
        return analyses.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static SemesterResultAnalysis createAnalysisIfNotZero(
            BigDecimal maxSGPA,
            BigDecimal avgSGPA,
            BigDecimal studentSGPA
    ) {
        // Check if avgSGPA is not zero
        return avgSGPA.compareTo(BigDecimal.ZERO) != 0
                ? new SemesterResultAnalysis(maxSGPA, avgSGPA, studentSGPA)
                : null;
    }
}
