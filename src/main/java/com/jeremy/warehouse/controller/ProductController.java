package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getProducts(){
        List<Product> products =  service.getAllProduct();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Product product = service.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        Product savedProduct = service.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = service.updateProduct(id, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletedProduct(@PathVariable Long id){
        Product product = service.getProductById(id);
        if(product!=null){
            service.deletedProduct(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update-quantity/{id}")
    public ResponseEntity<?> updateProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
        try {
            Product updatedProduct = service.updateProductQuantity(id, quantity);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
