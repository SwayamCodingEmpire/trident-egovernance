package com.trident.egovernance.global.services;

import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.entities.permanentDB.TestTable;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.repositories.permanentDB.SessionsRepository;
import com.trident.egovernance.global.repositories.permanentDB.TestTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SessionsRepository sessionsRepository;

    public SessionUpdateService(TestTableRepository testTableRepository, SessionsRepository sessionsRepository) {
        this.testTableRepository = testTableRepository;
        this.sessionsRepository = sessionsRepository;
    }

    @Transactional
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

    @Transactional
    public List<Sessions> updateTestTable1() {
        List<Sessions> testTableList = sessionsRepository.findAllByCourseOrderBySessionIdAscRegdYearAsc("B.TECH.");
        logger.info("Started");
        logger.info(testTableList.toString());

        // Group records by sessionId, startDate, course, and regdYear
        Map<String, List<Sessions>> groupedRecords = testTableList.stream()
                .collect(Collectors.groupingBy(record ->
                        record.getSessionId() + "|" +
                                record.getStartDate() + "|" +
                                record.getCourse() + "|" +
                                record.getRegdYear()
                ));

        // Process each group
        for (List<Sessions> group : groupedRecords.values()) {
            // Only process groups with exactly 2 records
            if (group.size() == 2) {
                logger.info("GROUP of students : {} ", group);
                // Sort by admissionYear in descending order
                group.sort((a, b) -> Integer.compare(b.getAdmissionYear(), a.getAdmissionYear()));

                // First record (higher admissionYear) gets LE
                Sessions laterEntry = group.getFirst();
                laterEntry.setStudentType("LE");
                logger.info("LE: " + laterEntry.getStudentType());
                logger.info(laterEntry.toString());
                sessionsRepository.save(laterEntry);

                // Second record (lower admissionYear) gets Regular
                Sessions regularEntry = group.get(1);
                regularEntry.setStudentType("REGULAR");
                logger.info("REGULAR: " + regularEntry.getStudentType());
                logger.info(regularEntry.toString());
                sessionsRepository.save(regularEntry);
            }
        }

        // Return updated records
        return sessionsRepository.findAllByCourseOrderBySessionIdAscRegdYearAsc("B.TECH.");
    }
}
