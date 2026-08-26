package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo repo;

    public CategoryRepo getRepo() {
        return repo;
    }

    @Autowired
    public void setRepo(CategoryRepo repo) {
        this.repo = repo;
    }

    public Category add(Category category){
        return repo.save(category);
    }

    public List<Category> getCategory(){
        return repo.findAll();
    }

    public Category getCategoryById(Long id) {
        return repo.findById(id).orElse(new  Category());
    }

    public Category deleteById(Long id) {
        Category category = repo.findById(id).orElse(new  Category());
        repo.delete(category);
        return category;
    }

}
