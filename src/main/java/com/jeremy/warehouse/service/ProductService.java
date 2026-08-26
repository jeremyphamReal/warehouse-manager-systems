package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.repository.CategoryRepo;
import com.jeremy.warehouse.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service

public class ProductService {
    @Autowired
    private ProductRepo repo;
    @Autowired
    private CategoryRepo categoryRepo;

    public List<Product> getAllProduct() {
        return repo.findAll();
    }

    public Product getProductById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Product> getCategoryIdFromProduct(Long id) {
        return repo.findByCategoryId(id);
    }

    public Product addProduct(Product product) throws NullPointerException {
        //addProduct(request):
        // 0. Kiểm tra product truyền vào có rỗng hay không
        if(product == null){
            throw new NullPointerException("Product is null");
        }
        //  1. Tìm Category theo categoryId trong request
        //     - nếu không thấy → ném lỗi rõ ràng (404 "category không tồn tại")
        //  2. Tạo Product mới, set category vừa tìm được (không tin category client gửi lên)
        if(product.getCategory().getId()==null || product.getCategory()==null){
            throw new IllegalArgumentException("Category id is null");
        }
        Category category = categoryRepo.findById(product.getCategory()
                .getId())
                .orElseThrow(() -> new IllegalArgumentException("Category id is null"));
        product.setCategory(categoryRepo.save(category));
        //  3. set id = null, createAt/updateAt = now
        if(product.getId()==null)
            product.setId(null);
        Date now = new Date();
        product.setCreateAt(now);
        product.setUpdateAt(now);
        //  4. set default quantity = 0, status = 1 nếu null
        if(product.getQuantity()==null)
            product.setQuantity(0);
        if(product.getStatus()==null)
            product.setStatus(1);
        //  5. save và trả về
        return repo.save(product);
    }


//    public void deletedProduct(Long id) {
//        Product existingProduct = repo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//        existingProduct.setStatus(0);
//        existingProduct.setUpdateAt(new Date());
//        repo.save(existingProduct);
//    }
//
//    public Product updateProductQuantity(Long id, int quantity) {
//        Product existingProduct = repo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        existingProduct.setQuantity(quantity);
//        existingProduct.setUpdateAt(new Date());
//
//        return repo.save(existingProduct);
//    }
//
//    public Product addProduct(Product product) {
//        Date now = new Date();
//        product.setId(null);
//        product.setCreateAt(now);
//        product.setUpdateAt(now);
//        if (product.getQuantity() == null) {
//            product.setQuantity(0);
//        }
//        if (product.getStatus() == null) {
//            product.setStatus(1);
//        }
//        return repo.save(product);
//    }

//    public Product updateProduct(Long id, Product product) {
//        Product existingProduct = repo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        existingProduct.setName(product.getName());
//        existingProduct.setSku(product.getSku());
//        existingProduct.setPrice(product.getPrice());
//        if (product.getQuantity() != null) {
//            existingProduct.setQuantity(product.getQuantity());
//        }
//        existingProduct.setDescription(product.getDescription());
//        existingProduct.setCategoryId(product.getCategoryId());
//        if (product.getStatus() != null) {
//            existingProduct.setStatus(product.getStatus());
//        }
//        existingProduct.setUpdateAt(new Date());
//
//        return repo.save(existingProduct);
//    }
}
