package co.com.bancolombia.s3.config.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.aws.s3")
public record S3ConnectionProperties(
        String bucketName,
        String region,
        String endpoint) {

}