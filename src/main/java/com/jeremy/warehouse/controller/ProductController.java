package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getProducts(){
        List<Product> products =  service.getAllProduct();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/search/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Product product = service.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) throws NullPointerException {
        Product savedProduct = service.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    //TODO: Hien thi danh sach product dua vao category
    @GetMapping("/list/{Id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<List<Product>> getAllProductInCategory(@PathVariable("Id") Long id){
        List<Product> productList = service.getCategoryIdFromProduct(id);
        if(productList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
    //TODO: Xoa mot product
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> deleteProductById(@PathVariable Long id){
        if(service.deleteProduct(id))
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    //TODO: Sua mot product trong category
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = service.updateProduct(id, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deletedProduct(@PathVariable Long id){
//        Product product = service.getProductById(id);
//        if(product!=null){
//            service.deletedProduct(id);
//            return new ResponseEntity<>("Deleted", HttpStatus.OK);
//        }
//        else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    @PutMapping("/update-quantity/{id}")
//    public ResponseEntity<?> updateProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
//        try {
//            Product updatedProduct = service.updateProductQuantity(id, quantity);
//            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
}
