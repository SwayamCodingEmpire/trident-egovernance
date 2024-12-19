package com.trident.egovernance.dto;

import java.util.List;
import java.util.Map;

public record StudentSubjectWiseResults(
        Map<Integer,List<SubjectResultData>> subjectResultDataList
) {
}
