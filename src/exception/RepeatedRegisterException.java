package exception;

/**
 * The repeated register exception throws when user try to register twice.
 */
public class RepeatedRegisterException extends Exception {
    /**
     * constructs a repeated register exception.
     * @param message the reply message
     */
    public RepeatedRegisterException(String message) {
        super(message);
    }
}
