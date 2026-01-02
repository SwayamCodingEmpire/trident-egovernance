package com.trident.egovernance.domains.resultUpload.services;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.entities.examDB.SubjectMaster;
import com.trident.egovernance.global.repositories.examDB.SubjectMasterRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
public class StudentResultProcessor implements ItemProcessor<StudentExamResults, StudentExamResults> {
    private static final Logger log = LoggerFactory.getLogger(StudentResultProcessor.class);

    private SubjectMasterRepo subjectMasterRepo;

    private String examType;

    private String academicYear;

    // Constructor injection for SubjectMasterRepo
    public StudentResultProcessor(SubjectMasterRepo subjectMasterRepo, String examType, String academicYear) {
        this.subjectMasterRepo = subjectMasterRepo;
        this.examType = examType;
        this.academicYear = academicYear;

    }
    @Override
    public StudentExamResults process(StudentExamResults item) throws Exception {
        // Log the item as it enters the processor
        log.debug("Processor: Incoming StudentExamResults item: RegdNo='{}', SubjectCode='{}', Grade='{}', Semester='{}'",
                item.getRegdno(), item.getSubjectCode(), item.getGrade(), item.getSemester());

        // ⭐ IMPORTANT: We will modify the 'item' object directly and return it.
        // No need to create a new 'StudentExamResults entity = new StudentExamResults();' here.
        // The 'item' already contains RegdNo, SubjectCode, Grade, and Semester from the reader.

        // --- Step 1: Add NULL CHECK BEFORE DB LOOKUP for SubjectCode ---
        if (item.getSubjectCode() == null || item.getSubjectCode().trim().isEmpty()) {
            log.warn("Processor: Skipping record for RegdNo '{}' because SubjectCode is null or empty. This item will be filtered out.", item.getRegdno());
            return null; // Return null to skip this item from being written
        }

        // --- Step 2: Perform DB lookup using the subjectCode from the input 'item' ---
        Optional<SubjectMaster> subjectOpt = subjectMasterRepo.findById(item.getSubjectCode());

        if (subjectOpt.isPresent()) {
            SubjectMaster subject = subjectOpt.get();
            item.setCredits(subject.getCredit()); // Set credits on the existing item

            // ⭐ Re-evaluate Semester setting here ⭐
            // The CustomExcelItemReader is already setting `item.setSemester(jobSemester.intValue());`
            // based on a job parameter. If the semester from SubjectMaster is different or meant to
            // override, then uncomment and use the line below. Otherwise, keep the semester from the job parameter.
            // For now, I'll assume job parameter semester is the intended one and this lookup is for credits.
            /*
            try {
                // Convert semester string from DB (e.g., "3rd") to integer
                // If subject.getSemester() is like "5th", "3rd", etc.
                item.setSemester(Integer.parseInt(subject.getSemester().replaceAll("\\D+","")));
            } catch (NumberFormatException e) {
                log.error("Processor: Failed to parse semester '{}' from database for subjectCode {}. Keeping existing semester. Error: {}",
                          subject.getSemester(), item.getSubjectCode(), e.getMessage());
                // If parsing fails, you might choose to keep the existing semester, set a default, or throw.
                // For now, it will retain the semester set by the reader.
            }
            */
        } else {
            // This scenario should ideally be caught by CustomExcelItemReader during header processing (in open method).
            // However, as a safeguard, if a subject somehow slips through or DB changes mid-job.
            log.error("Processor: Subject code '{}' not found in database during processing for RegdNo {}. This item will cause a job failure.", item.getSubjectCode(), item.getRegdno());
            // Throwing ParseException here will cause the job to fail for this item.
            // If you want to skip the item instead, return null.
            throw new ParseException("Subject code not found: " + item.getSubjectCode() + " for RegdNo: " + item.getRegdno());
            // return null; // Uncomment this and comment throw new ParseException if you want to skip
        }

        // --- Step 3: Set resultPublishDate from job parameters ---
        String resultPublishDate = String.format("%s (%s)", examType, academicYear);
        item.setResultPublishDate(resultPublishDate); // Set on the existing item

        // Log the item before returning it from the processor
        log.debug("Processor: Returning modified StudentExamResults item: RegdNo='{}', SubjectCode='{}', Grade='{}', Semester='{}', Credits='{}', ResultPublishDate='{}'",
                item.getRegdno(), item.getSubjectCode(), item.getGrade(), item.getSemester(), item.getCredits(), item.getResultPublishDate());

        return item; // Return the fully populated and modified item
    }
}
