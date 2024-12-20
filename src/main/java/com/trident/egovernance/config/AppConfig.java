package com.trident.egovernance.config;

import com.trident.egovernance.converters.DateConverters;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AppConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(DateConverters.utilToSqlConverter());
        modelMapper.addConverter(DateConverters.sqlToUtilConverter());
        return modelMapper;
    }

//    @Bean
//    public CacheManager cacheManager() {
//        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("userJobInformationCache");
//        cacheManager.setAllowNullValues(false);  // Example: Do not cache null values
//        return cacheManager;
//    }
    @Bean("taskExecutor")
    public Executor taskExecutor(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ExecutorService executorService() {
        return (ExecutorService) Executors.newVirtualThreadPerTaskExecutor();
    }
}
