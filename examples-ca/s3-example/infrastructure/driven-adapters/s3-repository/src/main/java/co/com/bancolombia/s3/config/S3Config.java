package co.com.bancolombia.s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import co.com.bancolombia.s3.config.model.S3ConnectionProperties;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Configuration
public class S3Config {

    @Bean
    @Profile({"dev", "cer", "pdn"})
    public S3Client s3Client(S3ConnectionProperties s3Properties) {
        return getBuilder(s3Properties).build();
    }

    @Bean
    @Profile("local")
    public S3Client localS3Client(AwsCredentials awsCredentials, S3ConnectionProperties s3Properties) {
        return getBuilder(s3Properties)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .endpointOverride(URI.create(s3Properties.endpoint()))
                .build();
    }

    private S3ClientBuilder getBuilder(S3ConnectionProperties s3Properties) {
        return S3Client.builder()
                .region(Region.of(s3Properties.region()));
    }

    @Bean
    @Profile("local")
    public AwsBasicCredentials getCredentials() {
        return AwsBasicCredentials.create("test", "test");
    }

}
