package exception;

/**
 * The invalid IrcServerMain arguments exception throws when an invalid configuration argument for server is entered.
 */
public class InvalidIrcServerMainArgumentsException extends Exception {
    /**
     * constructs an invalid IrcServerMain arguments exception.
     * @param message the reply message
     */
    public InvalidIrcServerMainArgumentsException(String message) {
        super(message);
    }
}
