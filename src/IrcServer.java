import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The IrcServer which allow users to chat.
 */
public class IrcServer {

    private String serverName;
    private ConcurrentHashMap<String, ArrayList<String>> channels;
    private ConcurrentHashMap<String, ConnectionHandler> registeredUsers;
    private ServerSocket serversocket;

    /**
     * constructs a Server with server name and port.
     * @param serverName the name of server
     * @param port the number of port
     */
    public IrcServer(String serverName, int port) {
        try {
            this.serverName = serverName;
            serversocket = new ServerSocket(port);
            channels = new ConcurrentHashMap<String, ArrayList<String>>();
            registeredUsers = new ConcurrentHashMap<String, ConnectionHandler>();
            System.out.println("Server started ... listening on port " + port + " ...");
            while (true) {
                Socket conn = serversocket.accept();
                System.out.println("Server got new connection request from " + conn.getInetAddress());

                ConnectionHandler ch = new ConnectionHandler(serverName, channels, registeredUsers, conn);
                ch.start();
            }
        } catch (IOException ioe) {
            System.out.println("Ooops " + ioe.getMessage());
        }
    }
}
