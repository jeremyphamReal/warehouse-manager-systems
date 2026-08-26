package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.service.CategoryService;
import com.jeremy.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService service;
    @Autowired
    private ProductService  productService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<Category>> getAll(){
        List<Category> categories =service.getCategory();
        return new  ResponseEntity<>(categories, HttpStatus.OK);
    }

    //TODO: Lay category dua theo ma
    @GetMapping("/search/{Id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Category> getById(@PathVariable("Id") Long id){
        Category category = service.getCategoryById(id);
        return ResponseEntity.ok().body(category);
    }
    //TODO: Xoa mot category dua theo ma
    @DeleteMapping("/delete/{Id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> deleteById(@PathVariable("Id") Long id){
        return new ResponseEntity<>(service.deleteById(id),HttpStatus.OK);
    }
    //TODO: Them moi mot category
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> add(@RequestBody Category category){
        Category savedCategory = service.add(category);
        return new ResponseEntity<>(savedCategory,HttpStatus.CREATED);
    }

}
