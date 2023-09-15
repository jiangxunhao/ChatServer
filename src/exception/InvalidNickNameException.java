package exception;

/**
 * The invalid nickname exception throws when user enter a invalid nickname.
 */
public class InvalidNickNameException extends Exception {
    /**
     * constructs an invalid nickname exception.
     * @param message the reply message
     */
    public InvalidNickNameException(String message) {
        super(message);
    }
}
