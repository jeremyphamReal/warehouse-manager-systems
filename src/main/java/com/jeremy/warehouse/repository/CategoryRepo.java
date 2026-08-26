package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {


    //Thành phần tương tác cũ trong việc học Udemy
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
//    //Read
//    public List<Category> findAll() {
//        String query =  "select * from category";
//
//        return jdbc.query(query, (rs, rowNum) ->{
//            Category category = new Category();
//
//            category.setId(rs.getLong("Id"));
//            category.setName(rs.getString("Name"));
//            category.setDescription(rs.getString("Description"));
//
//            return category;
//        });
//    }
//
//    //Created
//    public void save(Category category) {
//        String query = "insert into category (id, name, description) values (?,?,?)";
//        int rows = jdbc.update(query, category.getId(), category.getName(), category.getDescription());
//        System.out.println(rows + " effected");
//    }
//
//    //Deleted
//    public int deleteById(String id){
//        String query  = "DELETE FROM category WHERE id =?";
//        int rows = jdbc.update(query, id);
//        System.out.println(rows);
//        return rows;
//    }
}
