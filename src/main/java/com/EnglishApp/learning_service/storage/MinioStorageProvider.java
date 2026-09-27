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
@ConditionalOnProperty(name = "storage.provider", havingValue = "minio")
public class MinioStorageProvider implements StorageProvider {
	private final MinioClient client;
	private final StorageProperties properties;

	public MinioStorageProvider(StorageProperties properties) {
		this.properties = properties;
		this.client = MinioClient.builder()
				.endpoint(properties.getMinioEndpoint())
				.credentials(properties.getMinioAccessKey(), properties.getMinioSecretKey())
				.build();
	}

	@Override
	public void upload(String objectKey, String contentType, InputStream content, long contentLength) throws IOException {
		try {
			client.putObject(PutObjectArgs.builder()
					.bucket(properties.getMinioBucket())
					.object(objectKey)
					.stream(content, contentLength, -1)
					.contentType(contentType)
					.build());
		} catch (Exception exception) {
			throw new IOException("Could not upload object to MinIO", exception);
		}
	}

	@Override
	public PresignedUpload createUploadUrl(String objectKey, String contentType, long contentLength) {
		try {
			String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
					.method(Method.PUT)
					.bucket(properties.getMinioBucket())
					.object(objectKey)
					.expiry((int) properties.getPresignedUrlExpirySeconds(), TimeUnit.SECONDS)
					.build());
			return new PresignedUpload(objectKey, url, properties.getPresignedUrlExpirySeconds());
		} catch (Exception exception) {
			throw new IllegalStateException("Could not create MinIO upload URL", exception);
		}
	}

	@Override
	public void delete(String objectKey) {
		try {
			client.removeObject(RemoveObjectArgs.builder()
					.bucket(properties.getMinioBucket()).object(objectKey).build());
		} catch (Exception exception) {
			throw new IllegalStateException("Could not delete MinIO object", exception);
		}
	}

	@Override
	public String getUrl(String objectKey) {
		return properties.getMinioEndpoint().replaceAll("/$", "") + "/" + properties.getMinioBucket() + "/" + objectKey;
	}
}
