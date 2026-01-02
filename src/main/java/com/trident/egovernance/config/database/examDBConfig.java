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
@EnableJpaRepositories(basePackages = "com.trident.egovernance.global.repositories.examDB",
        entityManagerFactoryRef = "examEntityManagerFactory",
        transactionManagerRef = "examTransactionManager")
public class examDBConfig {
    @Bean(name = "permanentExamDBDataSource")
    @ConfigurationProperties(prefix = "spring.examdb.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "examEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder, @Qualifier("permanentExamDBDataSource") DataSource dataSource){
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        return builder.dataSource(dataSource)
                .properties(properties)
                .packages("com.trident.egovernance.global.entities.examDB")
                .persistenceUnit("examDB")
                .build();
    }

    @Bean(name = "examTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("examEntityManagerFactory") EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "dataSource")
    public DataSource springBatchDataSource(@Qualifier("permanentExamDBDataSource") DataSource examDataSource) {
        return examDataSource;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager springBatchTransactionManager(
            @Qualifier("examEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
