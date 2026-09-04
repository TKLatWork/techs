package me.webapp.service.timezone;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/timezone")
public class TimezoneController {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @GetMapping("/zones")
    public ZonesResponse zones() {
        List<String> zones = ZoneId.getAvailableZoneIds().stream().sorted().toList();
        return new ZonesResponse(zones);
    }

    @GetMapping("/convert")
    public ConvertResponse convert(@RequestParam String from,
                                   @RequestParam String to,
                                   @RequestParam String dateTime) {
        ZoneId fromZone = parseZone(from);
        ZoneId toZone = parseZone(to);
        LocalDateTime input;
        try {
            input = LocalDateTime.parse(dateTime, FORMAT);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dateTime must be in format yyyy-MM-dd'T'HH:mm");
        }
        ZonedDateTime source = input.atZone(fromZone);
        ZonedDateTime converted = source.withZoneSameInstant(toZone);
        return new ConvertResponse(
                fromZone.getId(),
                toZone.getId(),
                source.format(FORMAT),
                converted.format(FORMAT),
                source.getOffset().getId(),
                converted.getOffset().getId());
    }

    private ZoneId parseZone(String zone) {
        try {
            return ZoneId.of(zone);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown timezone: " + zone);
        }
    }
}
