package fiap.com.br.orderservice.exception;

public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(String message) {
        super(message);
    }
}
