package fiap.com.br.orderservice.service;

import fiap.com.br.orderservice.client.PaymentClient;
import fiap.com.br.orderservice.dto.OrderRequest;
import fiap.com.br.orderservice.entity.CustomerOrder;
import fiap.com.br.orderservice.entity.Dish;
import fiap.com.br.orderservice.exception.DishNotFoundException;
import fiap.com.br.orderservice.exception.OrderNotFoundException;
import fiap.com.br.orderservice.exception.OutOfStockException;
import fiap.com.br.orderservice.exception.PaymentFailedException;
import fiap.com.br.orderservice.repository.DishRepository;
import fiap.com.br.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;

    // NOVO
    private final PaymentClient paymentClient;

    @Transactional
    public CustomerOrder create(OrderRequest request) {

        Dish dish = dishRepository
                .findByIdForUpdate(request.dishId())
                .orElseThrow(() ->
                        new DishNotFoundException("Dish not found")
                );

        if (dish.getStock() < request.quantity()) {
            throw new OutOfStockException("Dish out of stock");
        }

        BigDecimal totalPrice = dish.getPrice()
                .multiply(
                        BigDecimal.valueOf(request.quantity())
                );

        dish.setStock(
                dish.getStock() - request.quantity()
        );

        dishRepository.save(dish);

        // NOVO
        try {

            paymentClient.pay(totalPrice);

        } catch (Exception exception) {

            throw new PaymentFailedException(
                    "Payment service unavailable"
            );
        }

        CustomerOrder order = new CustomerOrder(
                dish.getId(),
                request.quantity(),
                totalPrice,
                "CONFIRMED",
                LocalDateTime.now()
        );

        return orderRepository.save(order);
    }

    public CustomerOrder findById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found")
                );
    }
}