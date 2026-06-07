package com.techs.domain.userauth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class Session {
    private String token;
    private String userId;
    private Instant expiresAt;
    private Instant createdAt;
}
