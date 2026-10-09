package fiap.com.br.orderservice.exception;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
    super(message);
    }
}
