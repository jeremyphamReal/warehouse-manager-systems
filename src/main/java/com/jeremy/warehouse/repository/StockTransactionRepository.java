package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // Tìm theo type
    List<StockTransaction> findByType(StockTransactionType type);

    // Tìm theo type và product
    List<StockTransaction> findByProductIdAndType(Long productId, StockTransactionType type);

    // Thống kê số lượng nhập/xuất theo product
    @Query("SELECT SUM(t.quantityChange) FROM StockTransaction t " +
            "WHERE t.product.id = :productId AND t.type = :type")
    Integer sumQuantityChangeByProductAndType(
            @Param("productId") Long productId,
            @Param("type") StockTransactionType type
    );
}
