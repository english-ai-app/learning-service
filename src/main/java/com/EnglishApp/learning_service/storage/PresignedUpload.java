package com.EnglishApp.learning_service.storage;

public record PresignedUpload(String objectKey, String uploadUrl, long expiresInSeconds) {
}