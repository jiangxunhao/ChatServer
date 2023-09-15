package exception;

/**
 * The invalid username exception throws when user enter a username.
 */
public class InvalidUserNameException extends Exception {
    /**
     * constructs an invalid username exception.
     * @param message the reply message
     */
    public InvalidUserNameException(String message) {
        super(message);
    }
}
