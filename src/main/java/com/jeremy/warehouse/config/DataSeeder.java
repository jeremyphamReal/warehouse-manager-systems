//package com.jeremy.warehouse.config;
//
//import com.jeremy.warehouse.models.Category;
//import com.jeremy.warehouse.models.Product;
//import com.jeremy.warehouse.repository.CategoryRepo;
//import com.jeremy.warehouse.repository.ProductRepo;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//// @Component // Temporarily disabled
//@Profile("!test")
//
//public class DataSeeder implements CommandLineRunner {
//
//    private final CategoryRepo categoryRepo;
//    private final ProductRepo productRepo;
//
//    public DataSeeder(CategoryRepo categoryRepo, ProductRepo productRepo) {
//        this.categoryRepo = categoryRepo;
//        this.productRepo = productRepo;
//    }
//
//    @Override
//    public void run(String... args) {
//        List<Category> categories = seedCategories();
//        seedProducts(categories);
//    }
//
//    private List<Category> seedCategories() {
//        Map<String, Category> existingByName = categoryRepo.findAll().stream()
//                .collect(Collectors.toMap(Category::getName, Function.identity(), (left, right) -> left));
//
//        Category electronics = ensureCategory(existingByName, "Electronics", "Electronic devices and accessories");
//        Category furniture = ensureCategory(existingByName, "Furniture", "Home and office furniture");
//
//        return List.of(electronics, furniture);
//    }
//
//    private void seedProducts(List<Category> categories) {
//        if (productRepo.count() > 0) {
//            return;
//        }
//
//        Map<String, Category> categoryByName = categories.stream()
//                .collect(Collectors.toMap(Category::getName, Function.identity()));
//
//        Date now = new Date();
//        List<Product> products = List.of(
//                buildProduct("Laptop Pro 14", "SKU-001", 1899.0, 8, "High-performance laptop", categoryByName.get("Electronics"), now),
//                buildProduct("Wireless Mouse", "SKU-002", 29.9, 50, "Ergonomic wireless mouse", categoryByName.get("Electronics"), now),
//                buildProduct("Mechanical Keyboard", "SKU-003", 89.0, 30, "RGB mechanical keyboard", categoryByName.get("Electronics"), now),
//                buildProduct("Office Chair", "SKU-004", 159.0, 15, "Adjustable ergonomic chair", categoryByName.get("Furniture"), now),
//                buildProduct("Standing Desk", "SKU-005", 399.0, 6, "Height-adjustable desk", categoryByName.get("Furniture"), now)
//        );
//
//        productRepo.saveAll(products);
//    }
//
//    private Category ensureCategory(Map<String, Category> existingByName, String name, String description) {
//        Category category = existingByName.get(name);
//        if (category != null) {
//            return category;
//        }
//
//        category = buildCategory(name, description);
//        return categoryRepo.save(category);
//    }
//
//    private Category buildCategory(String name, String description) {
//        Category category = new Category();
//        category.setName(name);
//        category.setDescription(description);
//        return category;
//    }
//
//    private Product buildProduct(String name, String sku, Double price, Integer quantity, String description, Category category, Date now) {
//        Product product = new Product();
//        product.setName(name);
//        product.setSku(sku);
//        product.setPrice(price);
//        product.setQuantity(quantity);
//        product.setDescription(description);
//        product.setStatus(1);
//        product.setCreateAt(now);
//        product.setUpdateAt(now);
//        //product.setCategoryId(String.valueOf(category));
//        return product;
//    }
//}
