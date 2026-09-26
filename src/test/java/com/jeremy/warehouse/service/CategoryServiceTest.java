package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.repository.CategoryRepo;
import com.jeremy.warehouse.repository.ProductRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private CategoryService categoryService;

    //TODO: Them mot category moi throw loi khi trung ten
    @Test
    public void CategoryService_add_shouldThrowWhenAlreadyExistsName() {
        Category existingCategory = Category.builder()
                .name("Shirt")
                .description("new shirt").build();
        Category newCategory = Category.builder().name("Shirt").build();
        when(categoryRepo.findAll()).thenReturn(List.of(existingCategory));
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.add(newCategory));

        Mockito.verify(categoryRepo, Mockito.never()).save(Mockito.any());
    }

    //TODO: Xoa mot category dua vao id va throw loi neu tim khong thay
    @Test
    public void CategoryService_deleteById_shouldThrow_WhenCategoryIdNotExists(){
        Category categoryId = Category.builder().id(1L).build();
        when(categoryRepo.findById(categoryId.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           categoryService.deleteCategoryById(categoryId.getId());
        });
        Mockito.verify(categoryRepo, Mockito.never()).delete(Mockito.any());
    }

    //TODO: Xoa mot dua vao id khi tim thay se la success
    @Test
    public void CategoryService_deleteById_shouldDelete_whenNoProductsLinked() {
        Long existId = 1L;
        Category category = Category.builder().id(existId).name("Electronic").build();

        when(categoryRepo.findById(existId)).thenReturn(Optional.of(category));
        when(productRepo.existsByCategoryId(existId)).thenReturn(false);

        categoryService.deleteCategoryById(existId);
        Mockito.verify(categoryRepo, Mockito.times(1)).deleteById(existId);
    }

    //TODO: Xoa mot category khi no dang co product trong do
    @Test
    public void CategoryService_deleteCategory_shouldThrow_whenCategoryHasProducts() {
        Long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Toy").build();
        Product product = new Product();

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepo.existsByCategoryId(categoryId)).thenReturn(true);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> categoryService.deleteCategoryById(categoryId)
        );

        Assertions.assertEquals("Category have product can not be deleted", exception.getMessage());
        Mockito.verify(categoryRepo, Mockito.never()).deleteById(categoryId);
    }

}
