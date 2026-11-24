package pos.model;

/**
 * Thrown when a business rule is broken (e.g. not enough stock). The message is
 * shown to the user.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
