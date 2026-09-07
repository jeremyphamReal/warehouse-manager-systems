package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;
import com.jeremy.warehouse.models.User;
import com.jeremy.warehouse.models.UserPrincipal;
import com.jeremy.warehouse.repository.ProductRepo;
import com.jeremy.warehouse.repository.StockTransactionRepository;
import com.jeremy.warehouse.repository.UserRepo;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StockService {
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private StockTransactionRepository stockTransactionRepo;

    @Autowired
    private UserRepo userRepo;


    private static final int MAX_RETRIES = 3;


    public StockTransaction createStockTransaction(
            Long productId,
            StockTransactionType type,
            int quantityChange,
            Long userId) {

        // 1. Lấy product với pessimistic lock (khóa dữ liệu)
        Product product = productRepo.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại"));

        // 2. Kiểm tra và cập nhật quantity
        int oldQuantity = product.getQuantity();
        int newQuantity;

        if (type == StockTransactionType.OUT) {
            // Kiểm tra tồn kho
            if (product.getQuantity() < quantityChange) {
                throw new IllegalStateException(
                        String.format("Không đủ tồn kho. Hiện có: %d, Yêu cầu: %d",
                                product.getQuantity(), quantityChange)
                );
            }
            newQuantity = type.calculateQuantity(oldQuantity,  quantityChange);
        } else {
            newQuantity = type.calculateQuantity(oldQuantity, quantityChange);
        }

        // 3. Cập nhật quantity
        product.setQuantity(newQuantity);

        // 4. Lưu product
        Product savedProduct = productRepo.save(product);

        // 5. Tạo transaction record
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(savedProduct);
        transaction.setType(type);
        transaction.setQuantityChange(quantityChange);

        productRepo.save(savedProduct);

        transaction.setQuantityBefore(oldQuantity); // Lưu lại số lượng trước
        transaction.setQuantityAfter(newQuantity); // Lưu lại số lượng sau
        transaction.setPerformedBy(user);

        // 6. Lưu transaction
        return stockTransactionRepo.save(transaction);
    }

    public StockTransaction createStockTranscationWithRetry(
            Long productId, StockTransactionType type, int  quantityChange, Long userId)
    {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try{
                return createStockTransaction(productId, type, quantityChange, userId);
            }catch (OptimisticLockException e){
                retryCount++;
                if(retryCount == MAX_RETRIES){
                    throw new IllegalArgumentException("Hệ thống đang bận vui lòng thử lại sau. Lỗi:" +  e.getMessage());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        throw new IllegalStateException("Không thể xử lý giao dịch sau " + MAX_RETRIES + " lần thử");
    }
}
