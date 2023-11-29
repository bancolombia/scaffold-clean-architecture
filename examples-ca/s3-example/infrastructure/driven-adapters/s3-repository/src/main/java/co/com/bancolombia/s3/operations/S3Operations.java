package co.com.bancolombia.s3.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class S3Operations {

    private final S3Client s3Client;


    public boolean uploadObject(String bucketName, String objectKey, byte[] fileContent) {
        return s3Client.putObject(configurePutObject(bucketName, objectKey),
                RequestBody.fromBytes(fileContent)).sdkHttpResponse().isSuccessful();
    }

    public boolean uploadObject(String bucketName, String objectKey, String fileContent) {
        return s3Client.putObject(configurePutObject(bucketName, objectKey),
                RequestBody.fromString(fileContent)).sdkHttpResponse().isSuccessful();
    }

    public boolean uploadObject(String bucketName, String objectKey, File fileContent) {
        return s3Client.putObject(configurePutObject(bucketName, objectKey),
                RequestBody.fromFile(fileContent)).sdkHttpResponse().isSuccessful();
    }

    public List<S3Object> listBucketObjects(String bucketName) {
        return s3Client.listObjects(ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build()).contents();
    }

    public InputStream getObject(String bucketName, String objectKey) {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .key(objectKey)
                .bucket(bucketName)
                .build()).asInputStream();
    }

    public boolean deleteObject(String bucketName, String objectKey) {
        return s3Client.deleteObject(DeleteObjectRequest.builder()
                .key(objectKey)
                .bucket(bucketName).build()).sdkHttpResponse().isSuccessful();
    }

    private PutObjectRequest configurePutObject(String bucketName, String objectKey) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

}
