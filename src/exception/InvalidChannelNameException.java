package exception;

/**
 * The invalid channel name exception throws when user enter an invalid channel name.
 */
public class InvalidChannelNameException extends Exception {
    /**
     * constructs an invalid channel name.
     * @param message the reply message
     */
    public InvalidChannelNameException(String message) {
        super(message);
    }
}
