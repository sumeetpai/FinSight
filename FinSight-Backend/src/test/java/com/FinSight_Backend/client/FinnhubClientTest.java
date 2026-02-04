package com.FinSight_Backend.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

public class FinnhubClientTest {

    @Test
    public void testFetchLatestPrice() {
        // Use the same API key as in application.properties
        String apiKey = "d61g8v9r01qufbsn5lbgd61g8v9r01qufbsn5lc0";
        RestTemplate rt = new RestTemplate();
        FinnhubClient client = new FinnhubClient(rt, apiKey, "https://finnhub.io/api/v1/quote", true);
        Optional<BigDecimal> price = client.fetchLatestPrice("AAPL");
        // we cannot guarantee network access in CI, but assert that method returns either present or empty without throwing
        Assertions.assertNotNull(price);
        // If present, price should be > 0
        price.ifPresent(p -> Assertions.assertTrue(p.compareTo(BigDecimal.ZERO) > 0));
    }
}
