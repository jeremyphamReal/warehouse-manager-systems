package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");

        Date now = new Date();
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop");
        sampleProduct.setSku("SKU-001");
        sampleProduct.setPrice(999.99);
        sampleProduct.setQuantity(10);
        sampleProduct.setDescription("Gaming laptop");
        sampleProduct.setStatus(1);
        sampleProduct.setCreateAt(now);
        sampleProduct.setUpdateAt(now);
        sampleProduct.setCategory(category);
    }

    @Test
    void getProducts_returnsAllProducts() throws Exception {
        when(productService.getAllProduct()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"))
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[0].quantity").value(10));

        verify(productService).getAllProduct();
    }

    @Test
    void getProducts_returnsEmptyList() throws Exception {
        when(productService.getAllProduct()).thenReturn(List.of());

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(productService).getAllProduct();
    }

    @Test
    void getProductById_returnsProductWhenFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(get("/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_returnsNotFoundWhenMissing() throws Exception {
        when(productService.getProductById(99L)).thenReturn(null);

        mockMvc.perform(get("/product/99"))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(99L);
    }

    @Test
    void addProduct_returnsCreatedProduct() throws Exception {
        Product request = new Product();
        request.setName("Mouse");
        request.setSku("SKU-002");
        request.setPrice(29.99);
        request.setQuantity(50);
        request.setDescription("Wireless mouse");

        Product saved = new Product();
        saved.setId(2L);
        saved.setName(request.getName());
        saved.setSku(request.getSku());
        saved.setPrice(request.getPrice());
        saved.setQuantity(request.getQuantity());
        saved.setDescription(request.getDescription());
        saved.setStatus(1);

        when(productService.addProduct(any(Product.class))).thenReturn(saved);

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mouse",
                                  "sku": "SKU-002",
                                  "price": 29.99,
                                  "quantity": 50,
                                  "description": "Wireless mouse"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.sku").value("SKU-002"));

        verify(productService).addProduct(any(Product.class));
    }

    @Test
    void updateProduct_returnsUpdatedProduct() throws Exception {
        Product request = new Product();
        request.setName("Laptop Pro");
        request.setSku("SKU-001");
        request.setPrice(1299.99);
        request.setQuantity(5);
        request.setDescription("Updated laptop");

        Product updated = new Product();
        updated.setId(1L);
        updated.setName(request.getName());
        updated.setSku(request.getSku());
        updated.setPrice(request.getPrice());
        updated.setQuantity(request.getQuantity());
        updated.setDescription(request.getDescription());
        updated.setStatus(1);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updated);

        mockMvc.perform(put("/product/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Laptop Pro",
                                  "sku": "SKU-001",
                                  "price": 1299.99,
                                  "quantity": 5,
                                  "description": "Updated laptop"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1299.99));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_returnsNotFoundWhenProductMissing() throws Exception {
        Product request = new Product();
        request.setName("Unknown");

        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found with id: 99"));

        mockMvc.perform(put("/product/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Unknown"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with id: 99"));

        verify(productService).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    void deleteProduct_returnsOkWhenProductExists() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(delete("/product/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));

        verify(productService).getProductById(1L);
        verify(productService).deletedProduct(1L);
    }

    @Test
    void deleteProduct_returnsNotFoundWhenProductMissing() throws Exception {
        when(productService.getProductById(99L)).thenReturn(null);

        mockMvc.perform(delete("/product/delete/99"))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(99L);
        verify(productService, never()).deletedProduct(99L);
    }

    @Test
    void updateProductQuantity_returnsUpdatedProduct() throws Exception {
        Product updated = new Product();
        updated.setId(1L);
        updated.setName(sampleProduct.getName());
        updated.setQuantity(25);

        when(productService.updateProductQuantity(1L, 25)).thenReturn(updated);

        mockMvc.perform(put("/product/update-quantity/1").param("quantity", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(25));

        verify(productService).updateProductQuantity(1L, 25);
    }

    @Test
    void updateProductQuantity_returnsNotFoundWhenProductMissing() throws Exception {
        when(productService.updateProductQuantity(99L, 10))
                .thenThrow(new RuntimeException("Product not found with id: 99"));

        mockMvc.perform(put("/product/update-quantity/99").param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with id: 99"));

        verify(productService).updateProductQuantity(99L, 10);
    }
}
