package com.EnglishApp.learning_service.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String provider = "local";
    private String localRoot = "./storage";
    private String publicBaseUrl = "";
    private String minioEndpoint = "http://localhost:9000";
    private String minioAccessKey;
    private String minioSecretKey;
    private String minioBucket = "learning-service";
    private long presignedUrlExpirySeconds = 900;
    private long maxFileSizeBytes = 10485760;
}