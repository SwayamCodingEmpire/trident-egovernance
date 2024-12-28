package com.trident.egovernance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSS3Config {
    @Value("${aws.access.key}")
    private String accessKeyId;
    @Value("${aws.secret.key}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        Region region = Region.AP_SOUTH_1;
        return S3Client.builder()
                .credentialsProvider
                        (
                                StaticCredentialsProvider.create
                                        (
                                                AwsBasicCredentials.create
                                                        (
                                                                accessKeyId,
                                                                secretAccessKey
                                                        )
                                        )
                        )
                .region(region).
                build();
    }
}
