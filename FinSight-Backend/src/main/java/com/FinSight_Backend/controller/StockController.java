package com.FinSight_Backend.controller;
import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.service.StockService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@AllArgsConstructor
public class StockController {
    private StockService stockService;
    @PostMapping()
    public ResponseEntity<StocksDTO> addStocks(@RequestBody StocksDTO stocksDTO) {
        // Validate symbol presence (client sends stock_sym)
        if (stocksDTO == null || stocksDTO.getStock_sym() == null || stocksDTO.getStock_sym().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String symbol = stocksDTO.getStock_sym().trim();
        // If symbol exists, return existing stock (200 OK) with its id
        return stockService.getStocksBySymbol(symbol)
                .map(existing -> new ResponseEntity<>(existing, HttpStatus.OK))
                .orElseGet(() -> {
                    // Not found -> create and return 201 Created
                    StocksDTO savedStock = stockService.addStocks(stocksDTO);
                    return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
                });
    }

    @GetMapping("/{stock_id}")
    public ResponseEntity<StocksDTO> getStocks(@PathVariable Integer stock_id) {
        StocksDTO stocksDTO = stockService.getStocks(stock_id);
        return new ResponseEntity<>(stocksDTO, HttpStatus.OK);
    }

    @PutMapping("/{stock_id}")
    public ResponseEntity<StocksDTO> updateStocks(@PathVariable Integer stock_id, @RequestBody StocksDTO stocksDTO) {
        StocksDTO updatedStock = stockService.updateStocks(stock_id, stocksDTO);
        return new ResponseEntity<>(updatedStock, HttpStatus.OK);
    }

    @DeleteMapping("/{stock_id}")
    public ResponseEntity<String> deleteStocks(@PathVariable Integer stock_id) {
        String msg = stockService.deleteStocks(stock_id);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<StocksDTO> getBySymbol(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return stockService.getStocksBySymbol(symbol.trim())
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}