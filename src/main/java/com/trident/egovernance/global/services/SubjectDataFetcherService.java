package com.trident.egovernance.global.services;

import com.trident.egovernance.global.entities.permanentDB.Subject_Details;
import com.trident.egovernance.global.helpers.SubjectInfo;
import com.trident.egovernance.global.repositories.permanentDB.SubjectDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SubjectDataFetcherService {

    private static final Pattern SUBJECT_ROW_PATTERN = Pattern.compile(
            "<tr[^>]*>\\s*" +
                    "<td[^>]*>(\\d+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]*)</td>\\s*" +
                    "<td>([^<]*)</td>\\s*" +
                    "(?:<td><a href=\"([^\"]+)\"[^>]*>view</a></td>)?"
    );
    private final SubjectDetailsRepository subjectDetailsRepository;
    private final MapperService mapperService;

    public SubjectDataFetcherService(SubjectDetailsRepository subjectDetailsRepository, MapperService mapperService) {
        this.subjectDetailsRepository = subjectDetailsRepository;
        this.mapperService = mapperService;
    }

    public List<SubjectInfo> parseSubjectData(String htmlContent) {
        List<SubjectInfo> subjects = new ArrayList<>();

        Matcher matcher = SUBJECT_ROW_PATTERN.matcher(htmlContent);

        while (matcher.find()) {
            try {
                SubjectInfo subject = new SubjectInfo(
                        Integer.parseInt(matcher.group(1)),  // Serial Number
                        safelyTrim(matcher.group(2)),        // Subject Code
                        safelyTrim(matcher.group(3)),        // Subject Name
                        safelyTrim(matcher.group(4)),        // Course (B.Tech/M.Sc)
                        safelyTrim(matcher.group(5)),        // Year
                        safelyTrim(matcher.group(6)),        // Semester
                        matcher.group(7) != null ? matcher.group(7) : ""  // PDF Link
                );
                subjects.add(subject);
            } catch (Exception e) {
                // Log error for specific row without stopping entire parsing
                System.err.println("Error parsing row: " + matcher.group(0));
            }
        }

        return subjects;
    }

    /**
     * Safely trims the input string, handling null cases
     */
    public String safelyTrim(String input) {
        return input == null ? "" : input.trim();
    }

    public boolean saveSubjectDetails(List<SubjectTempDTO> subjectDetails){
        subjectDetailsRepository.saveAll(mapperService.convertToSubjectDetailsList(subjectDetails));
        return true;
    }
}
