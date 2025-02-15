package org.springboot.event_horizon.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {


    @Value("${aws.s3.access.key}")
    private  String awsS3accessKey ;

    @Value("${aws.s3.secret.key}")
    private String awsS3secretKey;

    @Value("${aws.region}")
    private String awsRegion;


    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(awsS3accessKey, awsS3secretKey);
        AmazonS3 amazonS3 =  AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsRegion)
                .build();
        return amazonS3;
    }
}
