package com.trident.egovernance.config;

import com.trident.egovernance.domains.resultUpload.services.StudentExamResultRepositoryImpl;
import com.trident.egovernance.domains.resultUpload.services.StudentResultProcessor;
import com.trident.egovernance.domains.resultUpload.utils.CustomExcelItemReader;
import com.trident.egovernance.domains.resultUpload.utils.CustomOracleItemWriter;
import com.trident.egovernance.domains.resultUpload.utils.JobCompletionNotificationListener;
import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import com.trident.egovernance.global.repositories.examDB.SubjectMasterRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    @Qualifier("permanentExamDBDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("examTransactionManager")
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ResultCustomDatabase resultCustomDatabase;

    @Autowired
    private SubjectMasterRepo subjectMasterRepo;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final StudentResultProcessor studentExamResultsProcessor; // ⭐ NEW: Inject your custom processor ⭐

    private final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    // ⭐ CORRECTED CONSTRUCTOR ⭐
    public BatchConfig(
                       JobCompletionNotificationListener jobCompletionNotificationListener,
                       StudentResultProcessor studentExamResultsProcessor // ⭐ ADDED HERE ⭐
    ) {
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
        this.studentExamResultsProcessor = studentExamResultsProcessor; // ⭐ ASSIGN HERE ⭐
    }

    // Existing JobRepository and TransactionManager beans remain the same
    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager); // Use the injected transactionManager
        factory.setDatabaseType("ORACLE");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager batchTransactionManager() {
        return transactionManager; // Reuse the transaction manager for the exam DB
    }

    @Bean("batchTaskExecutor")
    @Qualifier("batchTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring_batch_excel_upload-");
        logger.info("Starting Spring Batch Task Executor");
        return taskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository, @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    // ItemReader bean
    @Bean
    @JobScope
    public CustomExcelItemReader excelItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        CustomExcelItemReader excelItemReader = new CustomExcelItemReader(subjectMasterRepo);
        excelItemReader.setResource(new FileSystemResource(filePath));
        return excelItemReader;
    }

    // ⭐ REMOVE THE OLD excelRowToEntityProcessor BEAN DEFINITION HERE ⭐
    // You no longer define the processor as a lambda within BatchConfig.
    // The @Component-annotated StudentExamResultsProcessor class will be autowired/injected.


    @Bean
    public CustomOracleItemWriter oracleItemWriter() {
        return new CustomOracleItemWriter(resultCustomDatabase);
    }

    @Bean
    @JobScope // This is CRUCIAL! It makes the bean available only within a job execution.
    public StudentResultProcessor studentExamResultsProcessor(
            @Value("#{jobParameters['examType']}") String examType,
            @Value("#{jobParameters['academicYear']}") String academicYear) {
        // subjectMasterRepo is @Autowired at the class level, so it's available here.
        return new StudentResultProcessor(subjectMasterRepo,examType, academicYear);
    }

    @Bean
    public Step readAndSaveResultsStep(JobRepository jobRepository) {
        return new StepBuilder("readAndSaveResultsStep", jobRepository)
                .<StudentExamResults, StudentExamResults>chunk(800, transactionManager) // Chunk size of 800
                .reader(excelItemReader(null)) // filePath is provided by jobParameters at runtime
                .processor(studentExamResultsProcessor) // ⭐ USE THE INJECTED PROCESSOR INSTANCE ⭐
                .writer(oracleItemWriter())
                .build();
    }

    @Bean
    public Job importExamResultsJob(
            JobCompletionNotificationListener listener, // This parameter seems redundant if you inject it
            Step readAndSaveResultsStep,
            JobRepository jobRepository
    ) throws Exception {
        return new JobBuilder("importExamResultsJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // Good practice to have for unique job instances
                .listener(jobCompletionNotificationListener) // ⭐ Register the listener here using the injected instance ⭐
                .start(readAndSaveResultsStep)
                .build();
    }
}