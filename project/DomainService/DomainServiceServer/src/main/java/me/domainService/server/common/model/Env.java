package me.domainService.server.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Env {

    public static String STATUS_IDLE = "Idle";
    public static String STATUS_BOOKED = "Booked";
    public static String STATUS_RUNNING = "Running";

    private String name;
    private String status;
    private List<String> works = new ArrayList<>();
}
