package org.mrpaulwoods.customerportfolio.mapper;

import org.mrpaulwoods.customerportfolio.domain.Ticker;
import org.mrpaulwoods.customerportfolio.dto.CustomerInformation;
import org.mrpaulwoods.customerportfolio.dto.Holding;
import org.mrpaulwoods.customerportfolio.dto.StockTradeRequest;
import org.mrpaulwoods.customerportfolio.dto.StockTradeResponse;
import org.mrpaulwoods.customerportfolio.entity.Customer;
import org.mrpaulwoods.customerportfolio.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(
            Customer customer,
            List<PortfolioItem> items
    ) {
        var holdings = items.stream()
                .map(item -> new Holding(item.getTicker(), item.getQuantity()))
                .toList();

        return new CustomerInformation(
                customer.getId(),
                customer.getName(),
                customer.getBalance(),
                holdings
        );
    }

    public static PortfolioItem toPortfolioItem(Integer customerId, Ticker ticker) {
        var portfolioItem = new PortfolioItem();
        portfolioItem.setCustomerId(customerId);
        portfolioItem.setTicker(ticker);
        portfolioItem.setQuantity(0);
        return portfolioItem;
    }

    public static StockTradeResponse toStockTradeResponse(
            StockTradeRequest request,
            Integer customerId,
            Integer balance
    ) {
        return new StockTradeResponse(
                customerId,
                request.ticker(),
                request.price(),
                request.quantity(),
                request.action(),
                request.totalPrice(),
                balance
        );

    }
}
