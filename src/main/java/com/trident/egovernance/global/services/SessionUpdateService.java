package com.trident.egovernance.global.services;

import com.trident.egovernance.global.entities.permanentDB.TestTable;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.repositories.permanentDB.TestTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionUpdateService {
    private final Logger logger = LoggerFactory.getLogger(SessionUpdateService.class);
    private final TestTableRepository testTableRepository;

    public SessionUpdateService(TestTableRepository testTableRepository) {
        this.testTableRepository = testTableRepository;
    }

    public List<TestTable> updateTestTable() {
        List<TestTable> testTableList = testTableRepository.findAllByCourseOrderBySessionIdAscRegdYearAsc("B.TECH.");
        logger.info(testTableList.toString());
        List<TestTable> updatedTestTableList = new ArrayList<>();
        Map<String, Map<String,  Map<Integer, List<TestTable>>>> grouped = testTableList.stream()
                .collect(Collectors.groupingBy(
                        TestTable::getSessionId,
                                        Collectors.groupingBy(
                                                TestTable::getCourse,
                                                Collectors.groupingBy(
                                                        TestTable::getRegdYear
                                                )
                                        )
                ));
        List<TestTable> result = new ArrayList<>();

// Iterate over the grouped data
        for (Map<String,  Map<Integer, List<TestTable>>> courseMap : grouped.values()) {
            for (Map<Integer, List<TestTable>> regdYearMap : courseMap.values()) {
                        for (List<TestTable> sameRegdYearList : regdYearMap.values()) {
                            if (sameRegdYearList.size() == 2) {
                                logger.info(sameRegdYearList.toString());
                                // Sort by admissionYear in descending order
                                sameRegdYearList.sort(Comparator.comparingInt(TestTable::getAdmissionYear).reversed());

                                // Only update the StudentType, leave other fields intact
                                TestTable updatedFirst = new TestTable(sameRegdYearList.getFirst()); // Clone the first record
                                updatedFirst.setStudentType(StudentType.LE); // Set the StudentType to LE
                                logger.info(updatedFirst.toString());

                                TestTable updatedSecond = new TestTable(sameRegdYearList.get(1)); // Clone the second record
                                updatedSecond.setStudentType(StudentType.REGULAR); // Set the StudentType to REGULAR
                                logger.info(updatedSecond.toString());

                                // Add the modified records to the result list
                                result.add(updatedFirst);
                                result.add(updatedSecond);
                                logger.info(result.toString());
                            }
                        }
                    }
        }
        return testTableRepository.saveAllAndFlush(result);
    }
}
