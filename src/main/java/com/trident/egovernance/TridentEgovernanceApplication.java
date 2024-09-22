package com.trident.egovernance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
public class TridentEgovernanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TridentEgovernanceApplication.class, args);
    }

}
