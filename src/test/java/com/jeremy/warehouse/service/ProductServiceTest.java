package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.repository.CategoryRepo;
import com.jeremy.warehouse.repository.ProductRepo;
import com.jeremy.warehouse.repository.StockTransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
// Naming convention: methodName_shouldExpectedBehavior_whenCondition
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepo  productRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private StockTransactionRepository  stockTransactionRepository;
    @InjectMocks
    private ProductService productService;

    //TODO: add product thanh cong khi category ton tai
    @Test
    public void  addProduct_shouldSucceed_whenCategoryExists() {
        Category requestCategory = Category.builder().id(1L).build(); //truyền request input categoryId = 1
        Category existingCategory = Category.builder()
                .id(1L)
                .build(); //build một category ảo để test
        Product newProduct = Product.builder()
                .name("Electronic Bridge AI")
                .sku("SKU-BB-023")
                .quantity(19)
                .price(19.2)
                .description("new AI bridge")
                .category(requestCategory)
                .build(); //tạo mới 1 product

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(existingCategory)); //Nếu tìm category id = 1 thì coi như có tồn tại và trả existingCategory này ra
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product productToSave = invocation.getArgument(0);
            productToSave.setId(100L);
            return productToSave;
        });

        Product savedProduct = productService.addProduct(newProduct);

        assertNotNull(savedProduct);
        assertEquals(100L, savedProduct.getId());
        assertEquals(existingCategory, savedProduct.getCategory());
        assertEquals("Electronic Bridge AI", savedProduct.getName());
        assertNotNull(savedProduct.getCreateAt());
        assertNotNull(savedProduct.getUpdateAt());
        assertEquals(19, savedProduct.getQuantity());
        assertEquals(1, savedProduct.getStatus());
        verify(categoryRepo).findById(1L);
        verify(productRepo).save(any(Product.class));
    }

    //TODO: add product that bai khi category KHONG ton tai -> throw exception
    //NOTE: throw loi category is not found
    @Test
    public void addProduct_shouldThrow_whenCategoryIdNotExists(){
        Category requestCategory = Category.builder().id(9L).build();
        Category existingCategory = Category.builder()
                .id(1L)
                .build();
        Product newProduct = Product.builder()
                .name("Electronic Bridge AI")
                .sku("SKU-BB-023")
                .quantity(19)
                .price(19.2)
                .description("new AI bridge")
                .category(requestCategory)
                .build();

        when(categoryRepo.findById(anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(newProduct));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product productToSave = invocation.getArgument(0);
            productToSave.setId(100L);
            return productToSave;
        });

        Product savedProduct = productService.addProduct(newProduct);

        assertNotNull(savedProduct);
        assertEquals(100L, savedProduct.getId());
        assertEquals(existingCategory, savedProduct.getCategory());
        assertEquals("Electronic Bridge AI", savedProduct.getName());
        assertNotNull(savedProduct.getCreateAt());
        assertNotNull(savedProduct.getUpdateAt());
        assertEquals(19, savedProduct.getQuantity());
        assertEquals(1, savedProduct.getStatus());
        verify(categoryRepo).findById(anyLong());
        verify(productRepo).save(any(Product.class));
    }

    //TODO: xoa product that bai khi KHONG tim thay id -> throw exception
    @Test
    public void deleteProduct_shouldThrow_whenProductNotExists() {
        Long productId = 99L;
//        Product product = Product.builder().id(9L).build();
//        Long productId = product.getId();
        when(productRepo.findById(productId)).thenReturn(Optional.empty());

//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> productService.deleteProduct(productId)
//        );
//
//        assertEquals("Product id not found", exception.getMessage());
        Assertions.assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(productId));
        verify(productRepo).findById(productId);
        verify(productRepo, never()).delete(any(Product.class));
    }

    //TODO: xoa product that bai khi dang co lien ket voi stock transaction -> throw exception
    //NOTE: tránh mất dữ liệu stock transaction đang tham chiếu đến product
    @Test
    public void deleteProduct_shouldThrow_whenHaveStockTransaction() {
        Long productId = 99L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Wireless Mouse")
                .sku("SKU-MOU-001")
                .price(18.7)
                .quantity(34)
                .description("wireless mouse from logictech")
                .build();

        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(stockTransactionRepository.existsByProductId(productId)).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> productService.deleteProduct(productId)
        );

        assertEquals("Product has stock transactions and cannot be deleted", exception.getMessage());
        verify(productRepo).findById(productId);
        verify(stockTransactionRepository).existsByProductId(productId);
        verify(productRepo, never()).delete(any(Product.class));
    }

    //TODO: xoa product thanh cong khi KHONG con stock transaction nao lien ket
    @Test
    public void deleteProduct_shouldSuccess_whenDontHaveStockTransaction() {
        Long productId = 99L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Wireless Mouse")
                .sku("SKU-MOU-001")
                .price(18.7)
                .quantity(34)
                .description("wireless mouse from logictech")
                .build();

        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(stockTransactionRepository.existsByProductId(productId)).thenReturn(false);

        assertDoesNotThrow(() -> productService.deleteProduct(productId));
        verify(productRepo).delete(existingProduct);
        verify(productRepo).findById(productId);
        verify(stockTransactionRepository).existsByProductId(productId);
        verify(productRepo, times(1)).delete(any(Product.class));
    }

    //TODO: sua product - giu nguyen field KHONG duoc gui (vd: chi sua price, name phai giu nguyen)
    //NOTE: đây là hàm test chỉ cho update các field price, desc, name, createAt. Không cho update id, sku, updateAt, quantity
    @Test
    public void updateProduct_shouldKeepOldValues_whenFieldsNotProvided() {
        Long productId = 99L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Wireless Mouse")
                .sku("SKU-MOU-001")
                .price(18.7)
                .quantity(34)
                .description("wireless mouse from logictech")
                .build();
        Product updateRequest = Product.builder()
                .price(25.0)
                .build();
        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(productId, updateRequest);

        assertEquals(25.0, result.getPrice());
        assertEquals("Wireless Mouse", result.getName());
        assertEquals("SKU-MOU-001", result.getSku());
        assertEquals(34, result.getQuantity());
    }

    //TODO: sua product that bai khi product id KHONG ton tai -> throw exception
    @Test
    public void updateProduct_shouldThrow_whenProductIdNotExists() {
        Long productId = 99L;
        Product updateRequest = Product.builder()
                .name("Updated Mouse")
                .sku("SKU-MOU-999")
                .price(25.0)
                .build();

        when(productRepo.findById(productId)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> productService.updateProduct(productId, updateRequest)
        );

        assertEquals("Product not found with id: " + productId, exception.getMessage());
        verify(productRepo).findById(productId);
        verify(productRepo, never()).save(any(Product.class));
    }

    //TODO: sua product that bai khi category moi KHONG ton tai -> throw exception
    @Test
    public void updateProduct_shouldThrow_whenCategoryNotExists() {
        Long productId = 99L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Wireless Mouse")
                .sku("SKU-MOU-001")
                .price(18.7)
                .quantity(34)
                .description("wireless mouse from logictech")
                .build();
        Category existingCategory = Category.builder().id(1L).build();
        Product updateRequest = Product.builder()
                .name("Updated Mouse")
                .sku("SKU-MOU-999")
                .price(25.0)
                .category(existingCategory)
                .build();
        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepo.findById(existingCategory.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(productId, updateRequest));

        assertEquals("Category is not exists", exception.getMessage());
        verify(productRepo).findById(productId);
        verify(categoryRepo).findById(existingCategory.getId());
        verify(productRepo, never()).save(any(Product.class));
    }
}
