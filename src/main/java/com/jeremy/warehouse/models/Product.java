package com.jeremy.warehouse.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.util.Date;

@Entity
@Table(name = "product", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "sku")
    private String sku;
    @Column(name = "price")
    private Double price;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Integer status;
    @CreationTimestamp
    @Column(name = "create_at")
    private Date createAt;
    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Version
    private Long version;
}
