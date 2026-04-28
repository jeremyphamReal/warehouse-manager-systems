package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepo {
    private JdbcTemplate jdbc;

    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    @Autowired
    public void setJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Category> findAll() {
        String query =  "select * from category";

        return jdbc.query(query, (rs, rowNum) ->{
            Category category = new Category();

            category.setId(rs.getLong("Id"));
            category.setName(rs.getString("Name"));
            category.setDescription(rs.getString("Description"));

            return category;
        });
    }

    public void save(Category category) {
        String query = "insert into category (id, name, description) values (?,?,?)";
        int rows = jdbc.update(query, category.getId(), category.getName(), category.getDescription());
        System.out.println(rows + " effected");
    }
}
