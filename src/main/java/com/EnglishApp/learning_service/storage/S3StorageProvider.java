package com.EnglishApp.learning_service.storage;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
public class S3StorageProvider implements StorageProvider {
    private final MinioClient client;
    private final StorageProperties properties;

    public S3StorageProvider(StorageProperties properties) {
        if (properties.getS3AccessKey() == null || properties.getS3AccessKey().isBlank()
                || properties.getS3SecretKey() == null || properties.getS3SecretKey().isBlank()) {
            throw new IllegalStateException("S3 access key and secret key must be configured");
        }
        this.properties = properties;
        this.client = MinioClient.builder()
                .endpoint(properties.getS3Endpoint())
                .credentials(properties.getS3AccessKey(), properties.getS3SecretKey())
                .region(properties.getS3Region())
                .build();
    }

    @Override
    public void upload(String objectKey, String contentType, InputStream content, long contentLength)
            throws IOException {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getS3Bucket())
                    .object(objectKey)
                    .stream(content, contentLength, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception exception) {
            throw new IOException("Could not upload object to S3", exception);
        }
    }

    @Override
    public PresignedUpload createUploadUrl(String objectKey, String contentType, long contentLength) {
        try {
            String uploadUrl = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(properties.getS3Bucket())
                    .object(objectKey)
                    .expiry((int) properties.getPresignedUrlExpirySeconds(), TimeUnit.SECONDS)
                    .build());
            return new PresignedUpload(objectKey, uploadUrl, properties.getPresignedUrlExpirySeconds());
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create S3 upload URL", exception);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getS3Bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new IllegalStateException("Could not delete S3 object", exception);
        }
    }

    @Override
    public String getUrl(String objectKey) {
        String publicBaseUrl = properties.getS3PublicBaseUrl();
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return publicBaseUrl.replaceAll("/$", "") + "/" + objectKey;
        }
        return properties.getS3Endpoint().replaceAll("/$", "")
                + "/" + properties.getS3Bucket() + "/" + objectKey;
    }

    @Override
    public String resolveObjectKey(String url) {
        String publicBaseUrl = properties.getS3PublicBaseUrl();
        String prefix = publicBaseUrl == null || publicBaseUrl.isBlank()
                ? properties.getS3Endpoint().replaceAll("/$", "") + "/" + properties.getS3Bucket() + "/"
                : publicBaseUrl.replaceAll("/$", "") + "/";
        if (!url.startsWith(prefix)) {
            throw new IllegalArgumentException("Image URL does not belong to configured S3 storage");
        }
        return url.substring(prefix.length());
    }
}
