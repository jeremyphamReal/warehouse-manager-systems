package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Disabled
class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;

    @MockitoBean
    private CategoryService categoryService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Electronics");
        sampleCategory.setDescription("Electronic items");
    }

    @Test
    void testGetAll_returnsAllCategories() {
        when(categoryService.getCategory()).thenReturn(List.of(sampleCategory));

        var response = categoryController.getAll();

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals("Electronics", response.getBody().get(0).getName());
        verify(categoryService).getCategory();
    }

    @Test
    void testAddCategory() {
        Category newCategory = new Category();
        newCategory.setName("Furniture");
        newCategory.setDescription("Furniture items");

        var response = categoryController.addCategory(newCategory);

        assertNotNull(response);
        assertEquals("Furniture", response.getBody().getName());
        verify(categoryService).add(newCategory);
    }

    @Test
    void testUpdateCategory() {
        Category updated = new Category();
        updated.setName("Updated Electronics");
        updated.setDescription("Updated description");

        var response = categoryController.updateCategory(1L, updated);

        assertNotNull(response);
        assertEquals("Updated Electronics", response.getBody().getName());
        verify(categoryService).add(updated);
    }

    @Test
    void testDeleteCategory() {
        var response = categoryController.deleteCategory(1L);

        assertNotNull(response);
        assertEquals("Deleted", response.getBody());
        verify(categoryService).delete(1L);
    }
}
