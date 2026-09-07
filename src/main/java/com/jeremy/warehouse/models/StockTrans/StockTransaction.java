package com.jeremy.warehouse.models.StockTrans;

import com.jeremy.warehouse.models.Product;
import com.jeremy.warehouse.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Data
@Table(name="Stock_Trans", schema = "public")
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private StockTransactionType type; // IN hoặc OUT

    private Integer quantityChange;
    private Integer quantityBefore;
    private Integer quantityAfter;

    @ManyToOne
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @CreationTimestamp
    private LocalDateTime transactionTime;

    private String note;
}