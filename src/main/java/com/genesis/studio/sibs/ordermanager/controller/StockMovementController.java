package com.genesis.studio.sibs.ordermanager.controller;

import com.genesis.studio.sibs.ordermanager.dto.StockMovementDTO;
import com.genesis.studio.sibs.ordermanager.model.StockMovement;
import com.genesis.studio.sibs.ordermanager.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockMovements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    public ResponseEntity<StockMovementDTO> createStockMovement(@RequestBody StockMovement stockMovement) {
        StockMovementDTO dto = stockMovementService.createStockMovement(stockMovement);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllStockMovements() {
        List<StockMovement> stockMovements = stockMovementService.getAllStockMovements();
        return ResponseEntity.ok(stockMovements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getStockMovementById(@PathVariable Long id) {
        StockMovement stockMovement = stockMovementService.getStockMovementById(id);
        return ResponseEntity.ok(stockMovement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockMovementDTO> updateStockMovement(@PathVariable Long id, @RequestBody StockMovement stockMovement) {
        StockMovementDTO dto = stockMovementService.updateStockMovement(id, stockMovement);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        stockMovementService.deleteStockMovement(id);
        return ResponseEntity.noContent().build();
    }
}
