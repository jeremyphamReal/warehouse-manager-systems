package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.DTO.StockTransactionRequest;
import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;
import com.jeremy.warehouse.models.UserPrincipal;
import com.jeremy.warehouse.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/stock")
public class StockTransactionController {
        @Autowired
        private StockService stockService;

        @PostMapping("/transaction")
        public ResponseEntity<?> createTransaction(
                @RequestBody StockTransactionRequest request,
                @AuthenticationPrincipal UserPrincipal currentUser) {

            try {
                StockTransaction transaction = stockService.createStockTranscationWithRetry(
                        request.productId(), request.stockTransactionType(), request.quantityChange(), currentUser.getId()
                );
                return ResponseEntity.ok(transaction);
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", e.getMessage()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", e.getMessage()));
            }
        }
}
