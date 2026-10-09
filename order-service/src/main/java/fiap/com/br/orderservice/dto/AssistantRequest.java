package fiap.com.br.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank
        String question
) {
}