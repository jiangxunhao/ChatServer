package exception;

/**
 * The no channel exists exception throws when user enter a channel name doesn't exist.
 */
public class NoChannelExistsException extends Exception {
    /**
     * The no channel exists exception.
     * @param message the reply message
     */
    public NoChannelExistsException(String message) {
        super(message);
    }
}
