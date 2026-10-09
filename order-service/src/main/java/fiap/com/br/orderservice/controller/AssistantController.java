package fiap.com.br.orderservice.controller;

import fiap.com.br.orderservice.dto.AssistantRequest;
import fiap.com.br.orderservice.dto.AssistantResponse;
import fiap.com.br.orderservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<AssistantResponse> ask(
            @Valid @RequestBody AssistantRequest request
    ) {

        String answer =
                chatService.ask(request.question());

        return ResponseEntity.ok(
                new AssistantResponse(answer)
        );
    }
}