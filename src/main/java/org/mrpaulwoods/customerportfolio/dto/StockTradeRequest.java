package org.mrpaulwoods.customerportfolio.dto;

import org.mrpaulwoods.customerportfolio.domain.Ticker;
import org.mrpaulwoods.customerportfolio.domain.TradeAction;

public record StockTradeRequest(
        Ticker ticker,
        Integer price,
        Integer quantity,
        TradeAction action
) {
    public Integer totalPrice() {
        return price * quantity;
    }

}
