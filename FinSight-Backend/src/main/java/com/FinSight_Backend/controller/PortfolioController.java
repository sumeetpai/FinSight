package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.dto.AddStockRequestDTO;
import com.FinSight_Backend.dto.RemoveStockRequestDTO;
import com.FinSight_Backend.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("api/v1/portfolio/")
@AllArgsConstructor
public class PortfolioController {
    private PortfolioService portfolioService;

    @PostMapping()
    public ResponseEntity<PortfolioDTO> addPortfolio(@RequestBody PortfolioDTO portfolioDTO) {
        // For creation, require owning user_id
        if (portfolioDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PortfolioDTO savedPortfolio = portfolioService.addPortfolio(portfolioDTO);
        if (savedPortfolio == null) {
            // user does not exist or other validation failed
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedPortfolio, HttpStatus.CREATED);
    }

    @GetMapping("{portfolio_id}")
    public ResponseEntity<PortfolioDTO> getPortfolio(@PathVariable Integer portfolio_id) {
        PortfolioDTO portfolioDTO = portfolioService.getPortfolio(portfolio_id);
        if (portfolioDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(portfolioDTO, HttpStatus.OK);
    }

    @PutMapping("{portfolio_id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(@PathVariable Integer portfolio_id, @RequestBody PortfolioDTO portfolioDTO) {
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(portfolio_id, portfolioDTO);
        if (updatedPortfolio == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedPortfolio, HttpStatus.OK);
    }

    @DeleteMapping("{portfolio_id}")
    public ResponseEntity<String> deletePortfolio(@PathVariable Integer portfolio_id) {
        String msg = portfolioService.deletePortfolio(portfolio_id);
        if (msg == null) {
            return new ResponseEntity<>("Portfolio not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // -------------------- GET ALL PORTFOLIOS --------------------
    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getAllPortfolios() {
        List<PortfolioDTO> list = portfolioService.getAllPortfolios();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // -------------------- GET PORTFOLIOS BY USER --------------------
    @GetMapping("user/{user_id}")
    public ResponseEntity<List<PortfolioDTO>> getPortfoliosByUser(@PathVariable Integer user_id) {
        List<PortfolioDTO> list = portfolioService.getPortfoliosByUser(user_id);
        if (list == null || list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("{portfolio_id}/stocks")
    public ResponseEntity<PortfolioDTO> addStockToPortfolio(@PathVariable Integer portfolio_id, @RequestBody AddStockRequestDTO req) {
        if (req == null || req.getStock_id() == null || req.getUser_id() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Integer qty = req.getQty() != null ? req.getQty() : 1;
        PortfolioDTO updated = portfolioService.addStockToPortfolio(portfolio_id, req.getStock_id(), req.getUser_id(), qty);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("{portfolio_id}/stocks/{stock_id}")
    public ResponseEntity<PortfolioDTO> removeStockFromPortfolio(@PathVariable Integer portfolio_id,
                                                                 @PathVariable Integer stock_id,
                                                                 @RequestBody RemoveStockRequestDTO req) {
        if (req == null || req.getUser_id() == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Integer qty = req.getQty() != null ? req.getQty() : 1;
        PortfolioDTO updated = portfolioService.removeStockFromPortfolio(portfolio_id, stock_id, req.getUser_id(), qty);
        if (updated == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}