package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepo repo;

    public CategoryRepo getRepo() {
        return repo;
    }

    @Autowired
    public void setRepo(CategoryRepo repo) {
        this.repo = repo;
    }

    public void add(Category category){
        repo.save(category);
    }

    public List<Category> getCategory(){
        return repo.findAll();
    }
}
