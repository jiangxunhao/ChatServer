package exception;

/**
 * The invalid command input exception throws when user enter an invalid command.
 */
public class InvalidCommandInputException extends Exception {
    /**
     * construct an invalid command input exception.
     * @param message the reply message
     */
    public InvalidCommandInputException(String message) {
        super(message);
    }
}
