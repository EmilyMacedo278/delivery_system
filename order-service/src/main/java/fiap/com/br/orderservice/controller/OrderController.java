package fiap.com.br.orderservice.controller;

import fiap.com.br.orderservice.dto.OrderRequest;
import fiap.com.br.orderservice.entity.CustomerOrder;
import fiap.com.br.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<CustomerOrder> create(
            @Valid @RequestBody OrderRequest request
    ) {
        CustomerOrder order = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }

    @GetMapping
    public CustomerOrder findbyId(@PathVariable Long id){
        return service.findById(id);
    }
}