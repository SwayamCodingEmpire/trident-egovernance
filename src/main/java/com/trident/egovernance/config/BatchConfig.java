package com.trident.egovernance.config;

import com.trident.egovernance.domains.resultUpload.utils.CustomExcelItemReader;
import com.trident.egovernance.domains.resultUpload.utils.CustomOracleItemWriter;
import com.trident.egovernance.domains.resultUpload.utils.JobCompletionNotificationListener;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import org.apache.xmlbeans.ResourceLoader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;
    private ResourceLoader resourceLoader;

    private ResultCustomDatabase resultCustomDatabase;

    @Bean
    @JobScope
    public CustomExcelItemReader excelItemReader(@org.springframework.beans.factory.annotation.Value("#{jobParameters['filePath']}") String filePath) {
        CustomExcelItemReader excelItemReader = new CustomExcelItemReader();
        excelItemReader.setResource(new FileSystemResource(filePath));
        return excelItemReader;
    }

    @Bean
    @JobScope
    public ItemProcessor<StudentExamResults, StudentExamResults> excelRowToEntityProcessor(
            @org.springframework.beans.factory.annotation.Value("#{jobParameters['examType']}") String examType,
            @org.springframework.beans.factory.annotation.Value("#{jobParameters['semester']}") int semester,
            @org.springframework.beans.factory.annotation.Value("#{jobParameters['branch']}") String branch,
            @org.springframework.beans.factory.annotation.Value("#{jobParameters['academinYear']}") String academicYear
    ) {
        return item -> {
            StudentExamResults entity = new StudentExamResults();
            entity.setRegdno(item.getRegdno());

            try {
                entity.setSemester(semester);
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid semester format: " + item.getSemester(), e);
            }

            entity.setSubjectCode(item.getSubjectCode());
            entity.setGrade(item.getGrade());

            try {
                entity.setCredits(item.getCredits());
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid credits format: " + item.getCredits(), e);
            }

            String resultPublishDate = String.format("%s (%s)", examType, academicYear);
            entity.setResultPublishDate(resultPublishDate);

            return entity;
        };
    }

    @Bean
    public CustomOracleItemWriter oracleItemWriter() {
        return new CustomOracleItemWriter(resultCustomDatabase);
    }

    @Bean
    public Step readAndSaveResultsStep(
            CustomExcelItemReader excelItemReader,
            ItemProcessor<StudentExamResults, StudentExamResults> excelRowToEntityProcessor,
            CustomOracleItemWriter oracleItemWriter
    ) {
        return new StepBuilder("readAndSaveResultsStep", jobRepository)
                .<StudentExamResults, StudentExamResults>chunk(800, transactionManager)
                .reader(excelItemReader)
                .processor(excelRowToEntityProcessor)
                .writer(oracleItemWriter)
                .build();
    }


    public Job importExamResultsJob(
            JobCompletionNotificationListener listener,
            Step readAndSaveResultsStep
    ) {
        return new JobBuilder("importExamResultsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(readAndSaveResultsStep)
                .end()
                .build();
    }

}
