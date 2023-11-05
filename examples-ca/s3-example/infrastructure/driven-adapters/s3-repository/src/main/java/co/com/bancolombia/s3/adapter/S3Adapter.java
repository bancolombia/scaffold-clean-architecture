package co.com.bancolombia.s3.adapter;

import co.com.bancolombia.model.file.gateways.FileRepository;
import co.com.bancolombia.s3.config.model.S3ConnectionProperties;
import co.com.bancolombia.s3.operations.S3Operations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class S3Adapter implements FileRepository{

    private final S3Operations s3Operations;

    private final S3ConnectionProperties properties;

    @Override
    public boolean upload(byte[] bytes, String contentType, String name) {
        return s3Operations.uploadObject(properties.bucketName(), UUID.randomUUID() + "-" + name, bytes);
    }

    @Override
    public boolean delete(String fileUrl) {
        return s3Operations.deleteObject(properties.bucketName(), fileUrl);
    }

    @Override
    public List<String> list() {
        return s3Operations.listBucketObjects(properties.bucketName())
                .stream().map(S3Object::key).toList();
    }
}
