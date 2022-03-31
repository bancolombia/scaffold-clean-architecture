package co.com.bancolombia.s3.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.aws.s3")
@AllArgsConstructor
@RequiredArgsConstructor
public class S3ConnectionProperties {

    private String bucketName;
    private String region;
    private String endpoint;
}