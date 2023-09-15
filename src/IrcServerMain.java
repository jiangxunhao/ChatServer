import exception.InvalidIrcServerMainArgumentsException;

/**
 * The entry of the IrcServer, which open a server.
 */
public class IrcServerMain {
    /**
     * open a server with server name and port from command line.
     * @param args server name and port number
     */
    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                throw new InvalidIrcServerMainArgumentsException("Usage: java IrcServerMain <server_name> <port>");
            } else {
                if (!args[1].matches("^[0-9]+$")) {
                    throw new InvalidIrcServerMainArgumentsException("Usage: java IrcServerMain <server_name> <port>");
                }
                String serverName = args[0];
                int port = Integer.parseInt(args[1]);
                IrcServer irc = new IrcServer(serverName, port);
            }
        } catch (InvalidIrcServerMainArgumentsException invalidIrcServerMainArgumentsException) {
            System.out.println(invalidIrcServerMainArgumentsException.getMessage());
        }
    }
}
