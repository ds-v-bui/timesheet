package com.dsvn.starterkit.infrastructure.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsClient {

    @Autowired private AwsProperties awsProperties;

    private AWSStaticCredentialsProvider getAwsCredentials() {
        AWSCredentials credentials =
                new BasicAWSCredentials(
                        awsProperties.getCredentials().getAccessKey(),
                        awsProperties.getCredentials().getSecretKey());
        return new AWSStaticCredentialsProvider(credentials);
    }

    @Bean
    public AmazonSimpleEmailService simpleEmailServiceClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(getAwsCredentials())
                .withRegion(awsProperties.getRegion())
                .build();
    }

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getAwsCredentials())
                .withRegion(awsProperties.getRegion())
                .build();
    }
}
