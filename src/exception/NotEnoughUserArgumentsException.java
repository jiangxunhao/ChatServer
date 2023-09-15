package exception;

/**
 * The not enough user arguments exception throws when the parameters are not enough from user.
 */
public class NotEnoughUserArgumentsException extends Exception {
    /**
     * constructs a not enough user arguments exception.
     * @param message the reply message
     */
    public NotEnoughUserArgumentsException(String message) {
        super(message);
    }
}
