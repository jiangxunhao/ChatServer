package exception;

/**
 * The no user exists exception throws when user enter a nickname doesn't exist.
 */
public class NoUserExistsException extends Exception {
    /**
     * construct a no user exists exception.
     * @param message the reply message
     */
    public NoUserExistsException(String message) {
        super(message);
    }
}
