package com.jeremy.warehouse.models.DTO;

import com.jeremy.warehouse.models.StockTrans.StockTransactionType;

public record StockTransactionRequest(
        Long productId,
        Integer quantityChange,
        String note,
        StockTransactionType stockTransactionType
) { }
