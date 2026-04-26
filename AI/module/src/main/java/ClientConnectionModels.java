import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * Client Connection Management Models
 */
public class ClientConnectionModels {

    /**
     * Connect request model
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Connect {
        @NotBlank(message = "targetEnv is required")
        private String targetEnv;
        
        @Min(value = 1, message = "expiryMins must be at least 1 minute")
        private Integer expiryMins;
        
        @NotBlank(message = "owner is required")
        private String owner;
    }

    /**
     * Request model for submitting DSL requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "token is required")
        private String token;
        
        @NotBlank(message = "dsl is required")
        private String dsl;
    }

    /**
     * Disconnect request model
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Disconnect {
        @NotBlank(message = "token is required")
        private String token;
    }

    /**
     * Connection response model
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionResponse {
        private String token;
        private String targetEnv;
        private String owner;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private String status;
    }

    /**
     * API response wrapper
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
}
