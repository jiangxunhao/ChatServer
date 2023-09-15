package exception;

/**
 * The disconnected exception throws when user enter QUIT command.
 */
public class DisconnectedException extends Exception {
    /**
     * constructs a disconnected exception.
     * @param message the reply message
     */
    public DisconnectedException(String message) {
        super(message);
    }
}
