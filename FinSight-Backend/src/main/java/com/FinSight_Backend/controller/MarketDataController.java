package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.MarketInfoDTO;
import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.MarketRiskDTO;
import com.FinSight_Backend.dto.MarketHistoryDTO;
import com.FinSight_Backend.service.MarketDataClient;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/market")
@AllArgsConstructor
public class MarketDataController {
    private MarketDataClient marketDataClient;

    @GetMapping("/price/{symbol}")
    public ResponseEntity<MarketPriceDTO> getPrice(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        MarketPriceDTO dto = marketDataClient.getPrice(symbol.trim());
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/info/{symbol}")
    public ResponseEntity<MarketInfoDTO> getInfo(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        MarketInfoDTO dto = marketDataClient.getInfo(symbol.trim());
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/risk/{symbol}")
    public ResponseEntity<MarketRiskDTO> getRisk(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        MarketRiskDTO dto = marketDataClient.getRisk(symbol.trim());
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<MarketHistoryDTO> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1y") String period,
            @RequestParam(defaultValue = "1d") String interval
    ) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        MarketHistoryDTO dto = marketDataClient.getHistory(symbol.trim(), period, interval);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
