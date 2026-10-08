package fiap.com.br.orderservice.controller;

import fiap.com.br.orderservice.entity.Dish;
import fiap.com.br.orderservice.exception.DishNotFoundException;
import fiap.com.br.orderservice.repository.DishRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository repository;

    public DishController(DishRepository repository) {
        this.repository = repository;
    }
    @GetMapping
    public List<Dish> findAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Dish findById(@PathVariable Long id){

        return  repository.findById(id)
        .orElseThrow(() ->
                new DishNotFoundException("Dish not found")
        );
    }
}