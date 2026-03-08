package me.webServer.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewOrderRequest extends Order {

    @JsonIgnore
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @JsonIgnore
    @Override
    public double getPrice() {
        return super.getPrice();
    }
}

