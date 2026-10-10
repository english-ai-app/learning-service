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
    private String s3Endpoint = "https://s3.us-east-1.amazonaws.com";
    private String s3Region = "us-east-1";
    private String s3AccessKey;
    private String s3SecretKey;
    private String s3Bucket = "learning-service";
    private String s3PublicBaseUrl = "";
    private long presignedUrlExpirySeconds = 900;
    private long maxFileSizeBytes = 10485760;
}