package fiap.com.br.orderservice.client;

import fiap.com.br.orderservice.dto.PaymentRequest;
import fiap.com.br.orderservice.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Retryable(
            includes = RestClientException.class,
            maxRetries = 3,
            delay = 200,
            multiplier = 2,
            jitter = 100,
            maxDelay = 2000
    )
    public PaymentResponse pay(BigDecimal amount) {

        System.out.println("Calling PAYMENT-SERVICE...");

        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                new PaymentRequest(amount),
                PaymentResponse.class
        );

        System.out.println(
                "Payment approved by instance: "
                        + response.getInstance()
        );

        return response;
    }
}