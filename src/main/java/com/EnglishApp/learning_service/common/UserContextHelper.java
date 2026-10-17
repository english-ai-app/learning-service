package com.EnglishApp.learning_service.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class UserContextHelper {
    private static final String HEADER_ACTION_USER = "X-Action-User";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private UserContextHelper() {
    }

    public static Long getAuthenticatedUserId(HttpServletRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Request is missing");
        }

        // 1. Try from X-Action-User header (standard gateway propagated header)
        String actionUserHeader = request.getHeader(HEADER_ACTION_USER);
        if (actionUserHeader != null && !actionUserHeader.isBlank()) {
            Long userId = extractFromActionUserHeader(actionUserHeader);
            if (userId != null && userId > 0) {
                return userId;
            }
        }

        // 2. Try from X-User-Id header
        String userIdHeader = request.getHeader(HEADER_USER_ID);
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                long parsed = Long.parseLong(userIdHeader.trim());
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // 3. Try from Authorization Bearer JWT
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            Long userId = extractFromJwtToken(authHeader.substring(BEARER_PREFIX.length()).trim());
            if (userId != null && userId > 0) {
                return userId;
            }
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
    }

    private static Long extractFromActionUserHeader(String header) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(header);
            if (node.hasNonNull("id") && node.get("id").asLong() > 0) {
                return node.get("id").asLong();
            }
            if (node.hasNonNull("userId") && node.get("userId").asLong() > 0) {
                return node.get("userId").asLong();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Long extractFromJwtToken(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length >= 2) {
                byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
                JsonNode payload = OBJECT_MAPPER.readTree(new String(decoded, StandardCharsets.UTF_8));
                if (payload.hasNonNull("userId") && payload.get("userId").asLong() > 0) {
                    return payload.get("userId").asLong();
                }
                if (payload.hasNonNull("id") && payload.get("id").asLong() > 0) {
                    return payload.get("id").asLong();
                }
                if (payload.hasNonNull("sub")) {
                    try {
                        long sub = Long.parseLong(payload.get("sub").asText());
                        if (sub > 0) {
                            return sub;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
