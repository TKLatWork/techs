package me.vibe.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Sample response DTO for the hello endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hello response containing a message and timestamp")
public class HelloResponse {

    @Schema(description = "Greeting message", example = "Hello, World!")
    private String message;

    @Schema(description = "Server timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime timestamp;
}