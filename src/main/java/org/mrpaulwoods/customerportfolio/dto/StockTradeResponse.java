package org.mrpaulwoods.customerportfolio.dto;

import org.mrpaulwoods.customerportfolio.domain.Ticker;
import org.mrpaulwoods.customerportfolio.domain.TradeAction;

public record StockTradeResponse(
        Integer customerId,
        Ticker ticker,
        Integer price,
        Integer quantity,
        TradeAction action,
        Integer totalPrice,
        Integer balance
) {
}
