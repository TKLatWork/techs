package me.ddd.shop.domain;

import org.springframework.stereotype.Service;

@Service
public interface TradeRepo {
    Trade save(Trade trade);
    Trade get(String id);
}
