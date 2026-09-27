package com.EnglishApp.learning_service.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageProvider {
    void upload(String objectKey, String contentType, InputStream content, long contentLength) throws IOException;

    PresignedUpload createUploadUrl(
            String objectKey,
            String contentType,
            long contentLength
    );

    void delete(String objectKey);

    String getUrl(String objectKey);
}
