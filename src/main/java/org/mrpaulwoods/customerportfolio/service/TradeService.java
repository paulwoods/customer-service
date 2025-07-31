package org.mrpaulwoods.customerportfolio.service;

import org.mrpaulwoods.customerportfolio.dto.StockTradeRequest;
import org.mrpaulwoods.customerportfolio.dto.StockTradeResponse;
import org.mrpaulwoods.customerportfolio.entity.Customer;
import org.mrpaulwoods.customerportfolio.entity.PortfolioItem;
import org.mrpaulwoods.customerportfolio.exceptions.ApplicationExceptions;
import org.mrpaulwoods.customerportfolio.mapper.EntityDtoMapper;
import org.mrpaulwoods.customerportfolio.repository.CustomerRepository;
import org.mrpaulwoods.customerportfolio.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TradeService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public TradeService(
            CustomerRepository customerRepository,
            PortfolioItemRepository portfolioItemRepository
    ) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional
    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest request) {
        return switch (request.action()) {
            case BUY -> buyStock(customerId, request);
            case SELL -> sellStock(customerId, request);
        };
    }

    private Mono<StockTradeResponse> buyStock(Integer customerId, StockTradeRequest request) {
        var customerMono = customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .filter(c -> c.getBalance() >= request.totalPrice())
                .switchIfEmpty(ApplicationExceptions.insufficientBalance(customerId));

        var portfolioItemMono = portfolioItemRepository.findByCustomerIdAndTicker(customerId, request.ticker())
                .defaultIfEmpty(EntityDtoMapper.toPortfolioItem(customerId, request.ticker()));

        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(t -> this.executeBuy(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> executeBuy(
            Customer customer,
            PortfolioItem portfolioItem,
            StockTradeRequest request
    ) {
        customer.setBalance(customer.getBalance() - request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() + request.quantity());
        return saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> sellStock(Integer customerId, StockTradeRequest request) {

        var customerMono = customerRepository
                .findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));

        var portfolioItemMono = portfolioItemRepository
                .findByCustomerIdAndTicker(customerId, request.ticker())
                .filter(item -> item.getQuantity() >= request.quantity())
                .switchIfEmpty(ApplicationExceptions.insufficientShares(customerId));

        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(t -> this.executeSell(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> executeSell(
            Customer customer,
            PortfolioItem portfolioItem,
            StockTradeRequest request
    ) {
        customer.setBalance(customer.getBalance() + request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() - request.quantity());
        return saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> saveAndBuildResponse(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        StockTradeResponse response = EntityDtoMapper.toStockTradeResponse(stockTradeRequest, customer.getId(), customer.getBalance());
        return Mono.zip(customerRepository.save(customer), portfolioItemRepository.save(portfolioItem))
                .thenReturn(response);
    }

}
