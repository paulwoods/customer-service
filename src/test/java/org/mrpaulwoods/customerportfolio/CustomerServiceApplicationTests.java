package org.mrpaulwoods.customerportfolio;

import org.junit.jupiter.api.Test;
import org.mrpaulwoods.customerportfolio.domain.Ticker;
import org.mrpaulwoods.customerportfolio.domain.TradeAction;
import org.mrpaulwoods.customerportfolio.dto.StockTradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceApplicationTests {

    public static final Logger log = LoggerFactory.getLogger(CustomerServiceApplicationTests.class);

    @Autowired
    private WebTestClient client;

    @Test
    void customerInformation() {
        getCustomer(1, HttpStatus.OK)
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10_000)
                .jsonPath("$.holdings").isEmpty();
    }

    @Test
    void buyAndSell() {

        // buy

        var buyRequest1 = new StockTradeRequest(Ticker.GOOGLE, 100, 5, TradeAction.BUY);
        trade(2, buyRequest1, HttpStatus.OK)
                .jsonPath("$.customerId").isEqualTo(2)
                .jsonPath("$.ticker").isEqualTo("GOOGLE")
                .jsonPath("$.price").isEqualTo(100)
                .jsonPath("$.quantity").isEqualTo(5)
                .jsonPath("$.action").isEqualTo("BUY")
                .jsonPath("$.totalPrice").isEqualTo(500)
                .jsonPath("$.balance").isEqualTo(9_500);

        var buyRequest2 = new StockTradeRequest(Ticker.GOOGLE, 100, 10, TradeAction.BUY);
        trade(2, buyRequest2, HttpStatus.OK)
                .jsonPath("$.customerId").isEqualTo(2)
                .jsonPath("$.ticker").isEqualTo("GOOGLE")
                .jsonPath("$.price").isEqualTo(100)
                .jsonPath("$.quantity").isEqualTo(10)
                .jsonPath("$.action").isEqualTo("BUY")
                .jsonPath("$.totalPrice").isEqualTo(1000)
                .jsonPath("$.balance").isEqualTo(8_500);

        // check the holdings
        getCustomer(2, HttpStatus.OK)
                .jsonPath("$.name").isEqualTo("Mike")
                .jsonPath("$.balance").isEqualTo(8_500)
                .jsonPath("$.holdings.length()").isEqualTo(1)
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(15);

        // sell

        var sellRequest1 = new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL);
        trade(2, sellRequest1, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9_050)
                .jsonPath("$.totalPrice").isEqualTo(550);

        var sellRequest2 = new StockTradeRequest(Ticker.GOOGLE, 110, 10, TradeAction.SELL);
        trade(2, sellRequest2, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(10_150)
                .jsonPath("$.totalPrice").isEqualTo(1100);


        // check the holdings
        getCustomer(2, HttpStatus.OK)
                .jsonPath("$.name").isEqualTo("Mike")
                .jsonPath("$.balance").isEqualTo(10_150)
                .jsonPath("$.holdings.length()").isEqualTo(1)
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(0);
    }

    @Test
    void customerNotFound() {
        getCustomer(99999, HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=99999] not found.")
                .jsonPath("$.type").isEqualTo("http://example.com/problems/customer-not-found")
                .jsonPath("$.title").isEqualTo("Customer Not Found")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.instance").isEqualTo("/customers/99999");

        trade(99999, new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL), HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=99999] not found.")
                .jsonPath("$.type").isEqualTo("http://example.com/problems/customer-not-found")
                .jsonPath("$.title").isEqualTo("Customer Not Found")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.instance").isEqualTo("/customers/99999/trade");
    }

    @Test
    void insufficientBalance() {
        trade(3, new StockTradeRequest(Ticker.GOOGLE, 100, 101, TradeAction.BUY), HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=3] does not have enough funds to complete the transaction")
                .jsonPath("$.type").isEqualTo("http://example.com/problems/insufficient-balance")
                .jsonPath("$.title").isEqualTo("Insufficient Balance")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.instance").isEqualTo("/customers/3/trade");
    }

    @Test
    void insufficientShares() {
        trade(3, new StockTradeRequest(Ticker.GOOGLE, 100, 10, TradeAction.SELL), HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=3] does not have enough shares to complete the transaction")
                .jsonPath("$.type").isEqualTo("http://example.com/problems/insufficient-shares")
                .jsonPath("$.title").isEqualTo("Insufficient Shares")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.instance").isEqualTo("/customers/3/trade");
    }

    private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
        return client.get()
                .uri("/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

    private WebTestClient.BodyContentSpec trade(Integer customerId, StockTradeRequest request, HttpStatus expectedStatus) {
        return client.post()
                .uri("/customers/{customerId}/trade", customerId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

}
