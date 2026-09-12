package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.repository.CategoryRepo;
import com.jeremy.warehouse.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo repo;

    @Autowired
    private ProductRepo productRepo;

    public CategoryRepo getRepo() {
        return repo;
    }

    @Autowired
    public void setRepo(CategoryRepo repo) {
        this.repo = repo;
    }

    public Category add(Category category){
        boolean nameExists = repo.findAll()
                .stream()
                .anyMatch(c -> c.getName().equals(category.getName()));
        if(nameExists){
            throw new IllegalArgumentException("Category already exists");
        }
        return repo.save(category);
    }

    public List<Category> getCategory(){
        return repo.findAll();
    }

    public Category getCategoryById(Long id) {
        return repo.findById(id).orElse(new  Category());
    }

//    public Category deleteById(Long id) {
//        Category category = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
//        repo.delete(category);
//        return category;
//    }
    public void deleteCategoryById(Long id){
        //1. tìm category dựa theo id
        Category category = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        //2. tìm danh sách product có categoryId == id được gán vào để tìm
//        List<Product> productList = productRepo.findByCategoryId(category.getId());
        //3. kiểm tra danh sách xem nếu danh sách product == rỗng -> cho phép xóa category
        if(!productRepo.existsById(category.getId())){
            throw new IllegalStateException("Category have product can not be deleted");
        }
        repo.deleteById(id);
    }
}
