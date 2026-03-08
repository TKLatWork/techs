package me.webServer.api;


import me.webServer.api.model.NewOrderRequest;
import me.webServer.api.model.Order;
import me.webServer.api.model.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cook/v1")
public interface ICookApi {

    @PostMapping("/submit")
    Response<Order> submitOrder(@RequestBody NewOrderRequest newOrder);

    @GetMapping("/check/{orderId}")
    Response<Order> checkOrder(@PathVariable String orderId);

    @DeleteMapping("/cancel/{orderId}")
    Response<Order> cancelOrder(@PathVariable String orderId);
}
