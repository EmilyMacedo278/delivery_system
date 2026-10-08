package fiap.com.br.orderservice.repository;

import fiap.com.br.orderservice.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {
}