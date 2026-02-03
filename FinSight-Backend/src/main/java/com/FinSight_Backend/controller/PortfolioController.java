package com.FinSight_Backend.controller;
import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/portfolio/")
@AllArgsConstructor
public class PortfolioController {
    private PortfolioService portfolioService;

    @PostMapping()
    public ResponseEntity<PortfolioDTO> addPortfolio(@RequestBody PortfolioDTO portfolioDTO) {
        // For creation, portfolio_id should be null; require owning user_id and at least one stock or shares
        if (portfolioDTO == null || portfolioDTO.getUser_id() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PortfolioDTO savedPortfolio = portfolioService.addPortfolio(portfolioDTO);
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

}