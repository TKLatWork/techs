package me.task.domain.domain;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Health {

    static public Health up(String version) {
        Health health = new Health();
        health.setStatus("UP");
        health.setVersion(version);
        health.setCheckedAt(OffsetDateTime.now());
        return health;
    }

    String status; // e.g., "UP" or "DOWN"
    String version; // application version
    OffsetDateTime checkedAt;
}

