package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.DTO.StockTransactionRequest;
import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;
import com.jeremy.warehouse.models.User.UserPrincipal;
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

            System.out.println(">>> Controller received request:");
            System.out.println(">>>   productId = " + request.productId());
            System.out.println(">>>   type = " + request.type());
            System.out.println(">>>   quantityChange = " + request.quantityChange());
            try {
                StockTransaction transaction = stockService.createStockTransactionWithRetry(
                        request.productId(),
                        request.type(),
                        request.quantityChange(),
                        currentUser.getId()
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
