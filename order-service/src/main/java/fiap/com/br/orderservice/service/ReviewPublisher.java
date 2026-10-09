package fiap.com.br.orderservice.service;

import fiap.com.br.orderservice.config.RabbitConfig;
import fiap.com.br.orderservice.dto.ReviewMessage;
import fiap.com.br.orderservice.dto.ReviewRequest;
import fiap.com.br.orderservice.entity.Dish;
import fiap.com.br.orderservice.exception.DishNotFoundException;
import fiap.com.br.orderservice.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final DishRepository dishRepository;

    public void publish(ReviewRequest request) {

        Dish dish = dishRepository
                .findById(request.dishId())
                .orElseThrow(() ->
                        new DishNotFoundException("Dish not found")
                );

        ReviewMessage message = new ReviewMessage(
                dish.getId(),
                dish.getName(),
                request.rating(),
                request.comment()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                message
        );
    }
}