package com.jeremy.warehouse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "category", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

}
