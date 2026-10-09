package fiap.com.br.orderservice.controller;

import fiap.com.br.orderservice.dto.ReviewRequest;
import fiap.com.br.orderservice.service.ReviewPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewPublisher publisher;

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody ReviewRequest request
    ) {

        publisher.publish(request);

        return ResponseEntity.accepted().build();
    }
}