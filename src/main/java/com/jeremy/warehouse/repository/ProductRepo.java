package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ProductRepo {
    private JdbcTemplate jdbc;

    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    @Autowired
    public void setJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Product> findAll() {
        String query =  "select * from product";

        return jdbc.query(query, (rs, rowNum) ->{
           Product p = new Product();

//           p.setId();
//           p.setName();
//           p.setSKU();

            return p;
        });
    }
}
