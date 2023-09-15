package exception;

/**
 * The not register exception throws when user enter commands which need to register first.
 */
public class NotRegisterException extends Exception {
    /**
     * construct not register exception.
     * @param message the reply message
     */
    public NotRegisterException(String message) {
        super(message);
    }
}
