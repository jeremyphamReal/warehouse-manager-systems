package com.jeremy.warehouse.models.DTO;

import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;

import java.time.LocalDateTime;

public record StockTransactionResponse(
        Long id,
        String productName,
        StockTransactionType type,
        Integer quantityChange,
        Integer quantityBefore,
        Integer quantityAfter,
        String performedByUsername,
        LocalDateTime transactionTime
) {
    public static StockTransactionResponse from(StockTransaction t) {
        return new StockTransactionResponse(
                t.getId(),
                t.getProduct().getName(),
                t.getType(),
                t.getQuantityChange(),
                t.getQuantityBefore(),
                t.getQuantityAfter(),
                t.getPerformedBy().getUsername(),
                t.getTransactionTime()
        );
    }
}
