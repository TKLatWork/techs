package me.webapp.service.timezone;

public record ConvertResponse(
        String fromZone,
        String toZone,
        String input,
        String converted,
        String fromOffset,
        String toOffset) {
}
