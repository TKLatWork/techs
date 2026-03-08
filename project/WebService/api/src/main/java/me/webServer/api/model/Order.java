package me.webServer.api.model;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    private String orderId;
    private String clientName;
    private List<CookItem> items;
    private String status;
    private double price;
}
