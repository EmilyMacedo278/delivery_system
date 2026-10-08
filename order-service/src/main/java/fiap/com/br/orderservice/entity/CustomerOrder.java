package fiap.com.br.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dishId;
    private int quantity;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;

    public CustomerOrder(
            Long dishId,
            int quantity,
            BigDecimal totalPrice,
            String status,
            LocalDateTime createdAt
    ) {
        this.dishId = dishId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }
}