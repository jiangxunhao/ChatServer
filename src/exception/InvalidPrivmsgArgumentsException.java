package exception;

/**
 * The invalid PRIVMSG arguments exception throws when user enter a name cannot be found in channels or registered users.
 */
public class InvalidPrivmsgArgumentsException extends Exception {
    /**
     * constructs an invalid PRIVMSG arugments exception.
     * @param message the reply message
     */
    public InvalidPrivmsgArgumentsException(String message) {
        super(message);
    }
}
