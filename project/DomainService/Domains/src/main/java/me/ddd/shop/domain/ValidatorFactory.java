package me.ddd.shop.domain;

import org.springframework.stereotype.Service;

@Service
public class ValidatorFactory {

    public TradeValidator get(Trade trade) {
        return switch (trade.type) {
            case "A" -> new DefaultValidator();
            default -> throw new IllegalArgumentException("Unknown trade type: " + trade.type);
        };
    }

    static public class DefaultValidator implements TradeValidator {

        TradeRepo repo;

        @Override
        public boolean validate(Trade trade) {
            validate(trade.id);
            checkDuplicate(trade.id);
            return true;
        }

        private void validate(String id) {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("Trade ID cannot be null or empty");
            }
        }

        private void checkDuplicate(String id) {
            if (repo.get(id) != null) {
                throw new IllegalArgumentException("Trade with ID " + id + " already exists");
            }
        }
    }
}
