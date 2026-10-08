package fiap.com.br.orderservice.config;


import fiap.com.br.orderservice.entity.Dish;
import fiap.com.br.orderservice.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(DishRepository repository) {
        return args -> {

            if (repository.count() == 0) {
                repository.save(
                        new Dish(
                                "House Burger",
                                "Brioche bun, beef, cheese and house sauce",
                                new BigDecimal("39.90"),
                                10
                        )
                );

                repository.save(
                        new Dish(
                                "Chicken Sandwich",
                                "Grilled chiken sandwich",
                                new BigDecimal("29.90"),
                                15
                        )
                );

                repository.save(
                        new Dish(
                                "Veggie Burger",
                                "Vegetarian burger with vegetables",
                                new BigDecimal("34.90"),
                                20
                        )
                );
                repository.save(
                        new Dish(
                                "Caesar Salad",
                                "Salad with lettuce and parmesan",
                                new BigDecimal("27.90"),
                                25
                        )
                );


                repository.save(
                        new Dish(
                                "French Fries",
                                "Crispy french fries",
                                new BigDecimal("15.90"),
                                30
                        )
                );
            }
        };
    }
}