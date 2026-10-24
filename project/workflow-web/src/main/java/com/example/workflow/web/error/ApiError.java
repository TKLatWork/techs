package com.example.workflow.web.error;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}