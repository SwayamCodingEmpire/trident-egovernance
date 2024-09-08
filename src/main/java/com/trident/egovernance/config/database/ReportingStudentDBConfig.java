package com.trident.egovernance.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "reportingStudentEntityManagerFactory",
        transactionManagerRef = "reportingStudentTransactionManager",
        basePackages = {"com.trident.egovernance.repositories.reportingStudent"})
public class ReportingStudentDBConfig {
    @Bean(name = "reportingStudentDataSource")
    @ConfigurationProperties(prefix = "spring.reportingstudent.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "reportingStudentEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder, @Qualifier("reportingStudentDataSource") DataSource dataSource){
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        return builder.dataSource(dataSource)
                .properties(properties)
                .packages("com.trident.egovernance.entities.reportingStudent")
                .persistenceUnit("ReportingStudent")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("reportingStudentEntityManagerFactory") EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }
}
