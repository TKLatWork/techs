package me.ddd.shop.domain;

import org.springframework.stereotype.Service;

@Service
public class TradeApp {

    TradeRepo repo;
    TradeImportService importService;
    ValidatorFactory validatorFactory;

    public Trade newTrade(Trade trade) {
        validatorFactory.get(trade).validate(trade);
        trade.onNewTrade();
        return repo.save(trade);
    }

    public Trade importTrade(Trade trade) {
        ImportResult importResult = importService.importTrade(trade);
        trade.onImport(importResult);
        return repo.save(trade);
    }

    public Trade queryTrade(String id) {
        return repo.get(id);
    }

}
