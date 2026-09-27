package com.EnglishApp.learning_service.storage;

import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageProvider implements StorageProvider {
	private final Path root;
	private final StorageProperties properties;

	public LocalStorageProvider(StorageProperties properties) {
		this.properties = properties;
		this.root = Paths.get(properties.getLocalRoot()).toAbsolutePath().normalize();
	}

	@Override
	public void upload(String objectKey, String contentType, InputStream content, long contentLength) throws IOException {
		Path target = resolve(objectKey);
		Files.createDirectories(target.getParent());
		Files.copy(content, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public PresignedUpload createUploadUrl(String objectKey, String contentType, long contentLength) {
		return new PresignedUpload(objectKey, getUrl(objectKey), 0);
	}

	@Override
	public void delete(String objectKey) {
		try {
			Files.deleteIfExists(resolve(objectKey));
		} catch (IOException exception) {
			throw new IllegalStateException("Could not delete local object", exception);
		}
	}

	@Override
	public String getUrl(String objectKey) {
		String baseUrl = properties.getPublicBaseUrl();
		return baseUrl == null || baseUrl.isBlank() ? objectKey : baseUrl.replaceAll("/$", "") + "/" + objectKey;
	}

	private Path resolve(String objectKey) {
		Path target = root.resolve(objectKey).normalize();
		if (!target.startsWith(root)) {
			throw new IllegalArgumentException("Invalid storage object key");
		}
		return target;
	}

}
