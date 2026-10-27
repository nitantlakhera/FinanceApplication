package com.finance.inventory.web;

import com.finance.inventory.domain.InventoryItem;
import com.finance.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> addStock(@RequestBody AddStockRequest request) {
        return ResponseEntity.ok(inventoryService.addStock(request.productId(), request.quantity()));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> listInventory() {
        return ResponseEntity.ok(inventoryService.listInventory());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryItem> getItem(@PathVariable String productId) {
        return inventoryService.getItem(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record AddStockRequest(String productId, int quantity) {
    }
}
