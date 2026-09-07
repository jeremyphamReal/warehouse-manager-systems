package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.Category;
import com.jeremy.warehouse.models.Product;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
    List<Product> findByCategoryId(Long categoryId);
//    private JdbcTemplate jdbc;
//
//    public JdbcTemplate getJdbc() {
//        return jdbc;
//    }
//
//    @Autowired
//    public void setJdbc(JdbcTemplate jdbc) {
//        this.jdbc = jdbc;
//    }
//
//    public List<Product> findAll() {
//        String query =  "select * from product";
//
//        return jdbc.query(query, (rs, rowNum) ->{
//           Product p = new Product();
//
////           p.setId();
////           p.setName();
////           p.setSKU();
//
//            return p;
//        });
//    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
}
