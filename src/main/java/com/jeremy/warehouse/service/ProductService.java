package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepo repo;

    public List<Product> getAllProduct() {
        return repo.findAll();
    }

    public Product getProductById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void deletedProduct(Long id) {
        Product existingProduct = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        existingProduct.setStatus(0);
        existingProduct.setUpdateAt(new Date());
        repo.save(existingProduct);
    }

    public Product updateProductQuantity(Long id, int quantity) {
        Product existingProduct = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setQuantity(quantity);
        existingProduct.setUpdateAt(new Date());

        return repo.save(existingProduct);
    }

    public Product addProduct(Product product) {
        Date now = new Date();
        product.setId(null);
        product.setCreateAt(now);
        product.setUpdateAt(now);
        if (product.getQuantity() == null) {
            product.setQuantity(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        return repo.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setName(product.getName());
        existingProduct.setSku(product.getSku());
        existingProduct.setPrice(product.getPrice());
        if (product.getQuantity() != null) {
            existingProduct.setQuantity(product.getQuantity());
        }
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategoryId(product.getCategoryId());
        if (product.getStatus() != null) {
            existingProduct.setStatus(product.getStatus());
        }
        existingProduct.setUpdateAt(new Date());

        return repo.save(existingProduct);
    }
}
