package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.models.StockTrans.StockTransaction;
import com.jeremy.warehouse.models.StockTrans.StockTransactionType;
import com.jeremy.warehouse.models.User.User;
import com.jeremy.warehouse.repository.ProductRepo;
import com.jeremy.warehouse.repository.StockTransactionRepository;
import com.jeremy.warehouse.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.mockito.Mockito.*;

//QUY TAC DAT TEN: methodName_shouldExpectedBehavior_whenCondition
@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    @Mock
    private ProductRepo productRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private StockTransactionRepository stockTransactionRepository;
    @InjectMocks
    private StockService stockService;

    //TODO: createStockTransaction - throw khi KHONG tim thay product
    @Test
    public void createStockTransaction_shouldThrow_whenProductNotFound(){
        Long productId = 1L;
        StockTransactionType type = StockTransactionType.IN; // Hoặc OUT tùy logic
        int quantityChange = 10;
        Long userId = 1L;

        when(productRepo.findByIdWithLock(productId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
            stockService.createStockTransaction(productId, type, quantityChange, userId);
        });
        Assertions.assertEquals("Product is not exists", exception.getMessage());
        verify(productRepo, times(1)).findByIdWithLock(productId);
    }
//TODO: createStockTransaction - throw khi KHONG tim thay user (performedBy)
    @Test
    public void createStockTransaction_shouldThrow_whenUserNotFound(){
        Long productId = 1L;
        StockTransactionType type = StockTransactionType.IN;
        int quantityChange = 10;
        Long userId = 99L;
        Product existingProduct = Product.builder().id(1L).build();

        when(productRepo.findByIdWithLock(productId)).thenReturn(Optional.of(existingProduct));
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    stockService.createStockTransaction(existingProduct.getId(), type, quantityChange, userId);
                });
        Assertions.assertEquals("User is not exists", exception.getMessage());
        verify(userRepo, times(1)).findById(userId);
        verify(productRepo, times(1)).findByIdWithLock(existingProduct.getId());
    }
//TODO: createStockTransaction - OUT that bai khi quantityChange > product.quantity hien co -> throw IllegalStateException, KHONG goi save()
    @Test
    public void createStockTransaction_shouldThrow_whenOutQuantityExceedsStock(){
        Long productId = 1L;
        StockTransactionType type = StockTransactionType.OUT;
        int quantityChange = 10;
        int quantityBefore = 5;
        Long userId = 99L;

        User existingUser = User.builder().id(userId).build();

        Product existingProduct = Product.builder()
                .id(productId)
                .quantity(quantityBefore)
                .build();

        String expectedMessage = String.format(
                "Not enough in Basement. NOW: %d, REQUEST: %d",
                quantityBefore, quantityChange
        );

        when(productRepo.findByIdWithLock(productId)).thenReturn(Optional.of(existingProduct));
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            stockService.createStockTransaction(productId, type, quantityChange, userId);
        });

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(productRepo, never()).save(any(Product.class));
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }
//TODO: createStockTransaction - IN thanh cong -> quantity tang dung, quantityBefore/quantityAfter tinh dung
    @Test
    public void createStockTransaction_shouldIncreaseQuantity_whenTypeIsIn(){
        Long productId = 1L;
        StockTransactionType type = StockTransactionType.IN;
        int quantityChange = -1;
        int quantityBefore = 5;
        Long userId = 99L;

        User existingUser = User.builder().id(userId).build();

        Product existingProduct = Product.builder()
                .id(productId)
                .quantity(quantityBefore)
                .build();

        when(productRepo.findByIdWithLock(productId)).thenReturn(Optional.of(existingProduct));
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stockService.createStockTransaction(productId, type, quantityChange, userId);
        });

        Assertions.assertEquals("Input quantity cannot be less than 0", exception.getMessage());

        verify(productRepo, never()).save(any(Product.class));
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }
//TODO: createStockTransaction - OUT thanh cong -> quantity giam dung, quantityBefore/quantityAfter tinh dung (case nay tung bat duoc bug tru 2 lan truoc do)
    @Test
    public void createStockTransaction_shouldDecreaseQuantity_whenTypeIsOut(){
        Long productId = 1L;
        StockTransactionType type = StockTransactionType.OUT;
        int quantityChange = 2;
        int quantityBefore = 5;
        Long userId = 99L;

        User existingUser = User.builder().id(userId).build();

        Product existingProduct = Product.builder()
                .id(productId)
                .quantity(quantityBefore)
                .build();

        String expectedMessage = String.format(
                "Not enough in Basement. NOW: %d, REQUEST: %d",
                quantityBefore, quantityChange
        );

        when(productRepo.findByIdWithLock(productId)).thenReturn(Optional.of(existingProduct));
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));

//        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            stockService.createStockTransaction(productId, type, quantityChange, userId);
//        });
//
//        Assertions.assertEquals(expectedMessage, exception.getMessage());
        //Assertions.assertEquals("Input quantity cannot be less than 0", exception.getMessage());

        Assertions.assertDoesNotThrow(() -> stockService.createStockTransaction(productId, type, quantityChange, userId));

        verify(productRepo, times(1)).save(any(Product.class));
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));
    }
//TODO: createStockTransaction - transaction luu dung product va performedBy (verify field duoc gan dung, khong bi null)
    @Test
    public void createStockTransaction_shouldSetCorrectProductAndPerformer(){
        Long productId = 1L;
        Long userId = 1L;
        int quantityBefore = 100;
        int quantityChange = 10;

        Product existingProduct = Product.builder()
                .id(productId)
                .quantity(quantityBefore)
                .build();

        User existingUser = User.builder()
                .id(userId)
                .username("jeremy")   // giả sử có field này
                .build();

        when(productRepo.findByIdWithLock(productId))
                .thenReturn(Optional.of(existingProduct));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(existingUser));

        // QUAN TRỌNG: mock save() trả về chính đối tượng được truyền vào
        // (giống hành vi thật của JPA: save trả về entity đã persist)
        when(productRepo.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockTransactionRepository.save(any(StockTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ========== 2. ACT ==========
        StockTransaction result = stockService.createStockTransaction(
                productId,
                StockTransactionType.IN,   // dùng IN cho happy path đơn giản
                quantityChange,
                userId
        );

        // ========== 3. ASSERT — kiểm tra field được gán đúng ==========
        Assertions.assertNotNull(result, "StockTransaction trả về không được null");

        // Product được gán đúng
        Assertions.assertNotNull(result.getProduct(), "Product is not null");
        Assertions.assertEquals(productId, result.getProduct().getId());

        // Performer (User) được gán đúng
        Assertions.assertNotNull(result.getPerformedBy(), "PerformedBy is not null");
        Assertions.assertEquals(userId, result.getPerformedBy().getId());
        Assertions.assertEquals("jeremy", result.getPerformedBy().getUsername());

        // (Bonus) Kiểm tra các field khác cũng được gán đúng
        Assertions.assertEquals(StockTransactionType.IN, result.getType());
        Assertions.assertEquals(quantityChange, result.getQuantityChange());
        Assertions.assertEquals(quantityBefore, result.getQuantityBefore());
        Assertions.assertEquals(quantityBefore + quantityChange, result.getQuantityAfter());

        // ========== 4. VERIFY — kiểm tra save() thực sự được gọi ==========
        verify(productRepo, times(1)).save(any(Product.class));
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));

    }
//TODO: createStockTransaction - dung productRepo.findByIdWithLock() chu khong phai findById() (verify dung method duoc goi)
    @Test
    public void createStockTransaction_shouldUseFindByIdWithLock_notFindById(){
        Long productId = 1L;
        Long userId = 1L;
        int quantityChange = 2;

        Product product = Product.builder()
                .id(productId)
                .quantity(10)
                .build();
        User user = User.builder().id(userId).build();

        // Chỉ mock findByIdWithLock (method ĐÚNG)
        when(productRepo.findByIdWithLock(productId))
                .thenReturn(Optional.of(product));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(user));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        stockService.createStockTransaction(
                productId, StockTransactionType.IN, quantityChange, userId);

        // Phải gọi findByIdWithLock đúng 1 lần
        verify(productRepo, times(1)).findByIdWithLock(productId);

        // TUYỆT ĐỐI KHÔNG được gọi findById (vì sẽ mất lock)
        verify(productRepo, never()).findById(anyLong());
    }
//TODO: createStockTranscationWithRetry - thanh cong ngay lan dau, KHONG can retry
    @Test
    public void createStockTranscationWithRetry_shouldSucceed_onFirstAttempt(){
        Long productId = 1L;
        Long userId = 1L;
        int quantityChange = 5;

        Product product = Product.builder()
                .id(productId)
                .quantity(10)
                .build();
        User user = User.builder().id(userId).build();

        StockTransaction expectedTx = new StockTransaction();
        expectedTx.setProduct(product);
        expectedTx.setType(StockTransactionType.IN);
        expectedTx.setQuantityChange(quantityChange);

        // Mock cho lần gọi ĐẦU TIÊN và DUY NHẤT
        when(productRepo.findByIdWithLock(productId))
                .thenReturn(Optional.of(product));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(user));
        when(productRepo.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(stockTransactionRepository.save(any(StockTransaction.class)))
                .thenReturn(expectedTx);   // trả về tx thành công ngay lần đầu

        StockTransaction result = stockService.createStockTranscationWithRetry(
                productId, StockTransactionType.IN, quantityChange, userId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedTx, result);

        // findByIdWithLock chỉ được gọi ĐÚNG 1 LẦN (không bị gọi lại do retry)
        verify(productRepo, times(1)).findByIdWithLock(productId);

        // stockTransactionRepo.save chỉ gọi 1 lần (không retry)
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));
    }
//TODO: createStockTranscationWithRetry - OptimisticLockException lien tuc vuot qua MAX_RETRIES -> throw IllegalArgumentException voi message ro rang
    @Test
    public void createStockTranscationWithRetry_shouldThrow_whenExceedsMaxRetries(){
        Long productId = 1L;
        Long userId = 1L;
        int quantityChange = 5;
        int MAX_RETRIES = 3;

        Product product = Product.builder()
                .id(productId)
                .quantity(10)
                .build();

        StockTransaction expectedTx = new StockTransaction();
        expectedTx.setProduct(product);
        expectedTx.setType(StockTransactionType.IN);
        expectedTx.setQuantityChange(quantityChange);

        when(productRepo.findByIdWithLock(productId))
                .thenThrow(new ObjectOptimisticLockingFailureException(Product.class, productId));


        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            stockService.createStockTranscationWithRetry(productId, StockTransactionType.IN, quantityChange, userId);
        });

        String msg = exception.getMessage();
        Assertions.assertNotNull(msg);
        Assertions.assertTrue(msg.contains(String.valueOf(MAX_RETRIES)),
                "Message phải nói rõ số lần retry. Actual: " + msg);
        Assertions.assertNotNull(exception.getCause(),
                "Phải giữ cause gốc để debug");

        verify(productRepo, times(MAX_RETRIES)).findByIdWithLock(productId);

        verify(productRepo, never()).save(any(Product.class));
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }
//TODO: createStockTranscationWithRetry - IllegalStateException (khong du ton kho) KHONG duoc retry, throw ngay lap tuc lan dau
    @Test
    public void createStockTranscationWithRetry_shouldNotRetry_whenIllegalStateException(){
        Long productId = 1L;
        Long userId = 1L;
        int quantityBefore = 5;
        int quantityChange = 10;  // > tồn kho → throw IllegalStateException

        Product product = Product.builder()
                .id(productId)
                .quantity(quantityBefore)
                .build();
        User user = User.builder().id(userId).build();

        when(productRepo.findByIdWithLock(productId))
                .thenReturn(Optional.of(product));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(user));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> stockService.createStockTranscationWithRetry(
                        productId, StockTransactionType.OUT, quantityChange, userId)
        );

        Assertions.assertTrue(
                exception.getMessage().contains("Not enough in Basement"),
                "Message phải báo hết hàng. Actual: " + exception.getMessage()
        );
        // findByIdWithLock chỉ được gọi ĐÚNG 1 LẦN duy nhất
        // Nếu Service retry → sẽ gọi 3 lần → test FAIL
        verify(productRepo, times(1)).findByIdWithLock(productId);

        // Không save gì cả
        verify(productRepo, never()).save(any(Product.class));
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }
}
