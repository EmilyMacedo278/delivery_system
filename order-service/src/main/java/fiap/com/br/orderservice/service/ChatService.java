package fiap.com.br.orderservice.service;

import fiap.com.br.orderservice.entity.Dish;
import fiap.com.br.orderservice.repository.DishRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    public ChatService(
            ChatClient.Builder builder,
            DishRepository dishRepository
    ) {
        this.chatClient = builder.build();
        this.dishRepository = dishRepository;
    }

    public String ask(String question) {

        String menu = dishRepository.findAll()
                .stream()
                .map(this::formatDish)
                .collect(Collectors.joining("\n"));

        String systemMessage = """
                Você é o atendente virtual de um restaurante.

                Responda sempre de forma curta e em português.

                Responda apenas perguntas relacionadas ao restaurante,
                aos pratos, preços, ingredientes, estoque e sugestões.

                Se a pergunta não tiver relação com o restaurante,
                recuse educadamente e diga que só pode ajudar
                com assuntos do restaurante.

                Não invente pratos ou informações.
                Use somente o cardápio atual abaixo.

                CARDÁPIO ATUAL:
                %s
                """.formatted(menu);

        return chatClient
                .prompt()
                .system(systemMessage)
                .user(question)
                .call()
                .content();
    }

    private String formatDish(Dish dish) {

        return """
                %s
                descrição: %s
                preço: R$ %s
                estoque: %d
                """.formatted(
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getStock()
        );
    }
}