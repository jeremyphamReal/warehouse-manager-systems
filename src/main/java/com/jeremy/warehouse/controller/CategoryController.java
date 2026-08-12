package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getAll(){
        List<Category> categories =service.getAllCategory();
        return new  ResponseEntity<>(categories, HttpStatus.OK);
    }
}
