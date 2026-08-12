package com.jeremy.warehouse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user", schema = "public")
@Getter
@Setter
@Component
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String role;
}
