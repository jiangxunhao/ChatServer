import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.DisconnectedException;
import exception.NoChannelExistsException;
import exception.NoUserExistsException;
import exception.InvalidChannelNameException;
import exception.InvalidNickNameException;
import exception.InvalidUserNameException;
import exception.NotEnoughUserArgumentsException;
import exception.RepeatedRegisterException;
import exception.InvalidCommandInputException;

/**
 * The class handles the user's connection, and extends the thread to support many users connect at same time.
 */
public class ConnectionHandler extends Thread {

    private Socket conn;
    private String serverName;
    private ConcurrentHashMap<String, ArrayList<String>> channels;
    private ConcurrentHashMap<String, ConnectionHandler> registeredUsers;

    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private PrintWriter pw;
    private String nickName = "*";
    private String userName;
    private String realName;

    /**
     * constructs a connection with server.
     * @param serverName the server name
     * @param channels the current channels existing in the server
     * @param registeredUsers the current users existing in the server
     * @param conn the socket used to communicate with server
     */
    public ConnectionHandler(String serverName, ConcurrentHashMap<String, ArrayList<String>> channels,
                             ConcurrentHashMap<String, ConnectionHandler> registeredUsers, Socket conn) {
        this.serverName = serverName;
        this.channels = channels;
        this.registeredUsers = registeredUsers;
        this.conn = conn;

        try {
            is = conn.getInputStream();
            os = conn.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
            pw = new PrintWriter(new OutputStreamWriter(os));
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler: " + ioe.getMessage());
            cleanup();
        }
    }

    /**
     * handles the user's request.
     */
    public void run() {
        System.out.println("new ConnectionHandler thread started ... ");
        try {
            handleClientRequest();
        } catch (Exception e) {
            cleanup();
        }
    }

    private void handleClientRequest() throws DisconnectedException, IOException {
        while (true) {
            try {
                String line = br.readLine();
                Pattern pattern = Pattern.compile("^([A-Z]+)\\s?(.*)");
                Matcher matcher = pattern.matcher(line);
                String command, arguments;

                if (matcher.matches()) {
                    command = matcher.group(1);
                    arguments = matcher.group(2);
                } else {
                    throw new InvalidCommandInputException("invalid command line input!");
                }

                CommandHandler commandHandler = new CommandHandler(arguments, this);

                switch (command) {
                    case Configuration.NICK_STRING:
                        commandHandler.handleNick();
                        break;
                    case Configuration.USER_STRING:
                        commandHandler.handleUser();
                        break;
                    case Configuration.QUIT_STRING:
                        commandHandler.handleQuit();
                        break;
                    case Configuration.JOIN_STRING:
                        commandHandler.handleJoin();
                        break;
                    case Configuration.PART_STRING:
                        commandHandler.handlePart();
                        break;
                    case Configuration.PRIVMSG_STRING:
                        commandHandler.handlePrivmsg();
                        break;
                    case Configuration.NAMES_STRING:
                        commandHandler.handleNames();
                        break;
                    case Configuration.LIST_STRING:
                        commandHandler.handleList();
                        break;
                    case Configuration.TIME_STRING:
                        commandHandler.handleTime();
                        break;
                    case Configuration.INFO_STRING:
                        commandHandler.handleInfo();
                        break;
                    case Configuration.PING_STRING:
                        commandHandler.handlePing();
                        break;
                    default:
                        throw new InvalidCommandInputException("cannot find the command!");
                }
            } catch (InvalidCommandInputException invalidCommandInputException) {
                sendError(invalidCommandInputException.getMessage());
            }
        }
    }

    /**
     * sends message to all registered users.
     * @param line the message content
     */
    public void sendAllMessage(String line) {
        Iterator<String> iterator = registeredUsers.keySet().iterator();
        while (iterator.hasNext()) {
            String targetNickName = iterator.next();
            sendUserMessage(targetNickName, line);
        }
    }

    /**
     * sends private message to all users in the channel.
     * @param targetChannelName the name of target channel
     * @param message the message content
     * @throws NoChannelExistsException cannot find the existing channel
     */
    public void sendChannelPrivmsgMessage(String targetChannelName, String message) throws NoChannelExistsException {
        if (channels.containsKey(targetChannelName)) {
            ArrayList<String> inChannelUsers = channels.get(targetChannelName);
            Iterator<String> iterator = inChannelUsers.iterator();
            while (iterator.hasNext()) {
                String targetNickName = iterator.next();
                String line = ":" + nickName + " " + Configuration.PRIVMSG_STRING + " " + targetChannelName + " :" + message;
                sendUserMessage(targetNickName, line);
            }
        } else {
            throw new NoChannelExistsException("No channel exists with that name");
        }
    }

    /**
     * sends the private message to the user.
     * @param targetNickName the nickname of user
     * @param message the message content
     * @throws NoUserExistsException cannot find the existing user
     */
    public void sendUserPrivmsgMessage(String targetNickName, String message) throws NoUserExistsException {
        if (registeredUsers.containsKey(targetNickName)) {
            String line = ":" + nickName + " " + Configuration.PRIVMSG_STRING + " " + targetNickName + " :" + message;
            sendUserMessage(targetNickName, line);
        } else {
            throw new NoUserExistsException("No user exists with that name");
        }
    }

    /**
     * sends the message to all users in the channel, this message content is same for every user.
     * @param targetChannelName the name of channel
     * @param line the message content
     */
    public void sendChannelMessage(String targetChannelName, String line) {
        ArrayList<String> inChannelUsers = channels.get(targetChannelName);
        Iterator<String> iterator = inChannelUsers.iterator();
        while (iterator.hasNext()) {
            String targetNickName = iterator.next();
            sendUserMessage(targetNickName, line);
        }
    }

    /**
     * sends the message to the user.
     * @param targetNickName the nickname of user
     * @param line the message content
     */
    public void sendUserMessage(String targetNickName, String line) {
        ConnectionHandler target = registeredUsers.get(targetNickName);
        target.send(line);
    }

    /**
     * leave the current channel.
     * @param arguments the name of channel
     */
    public void partChannel(String arguments) {
        ArrayList<String> inChannelUsers = channels.get(arguments);
        if (inChannelUsers.contains(nickName)) {
            inChannelUsers.remove(nickName);
        }
        if (inChannelUsers.isEmpty()) {
            channels.remove(arguments);
        }
    }

    /**
     * creates a new channel in the server.
     * @param arguments the channel name
     * @throws InvalidChannelNameException invalid channel name
     */
    public void createChannel(String arguments) throws InvalidChannelNameException {
        Pattern channelNamePattern = Pattern.compile("^\\#[\\w]+");
        Matcher channelNameMatcher = channelNamePattern.matcher(arguments);
        if (channelNameMatcher.matches()) {
            ArrayList<String> inChannelUsers = new ArrayList<String>();
            inChannelUsers.add(nickName);
            channels.put(arguments, inChannelUsers);
        } else {
            throw new InvalidChannelNameException("Invalid channel name");
        }
    }

    /**
     * joins a existing channel.
     * @param arguments the channel name
     */
    public void joinChannel(String arguments) {
        channels.get(arguments).add(nickName);
    }

    /**
     * sets the nickname of this user.
     * @param nickName the nickname user wants to set
     * @throws InvalidNickNameException invalid nickname
     */
    public void setNickName(String nickName) throws InvalidNickNameException {
        Pattern nickNamePattern = Pattern.compile("^[A-Za-z_][\\w]{0,8}");
        Matcher nickNameMatcher = nickNamePattern.matcher(nickName);
        if (nickNameMatcher.matches()) {
            this.nickName = nickName;
        } else {
            throw new InvalidNickNameException("Invalid nickname");
        }
    }

    /**
     * registers in the server.
     * @param arguments include the username and real name.
     * @throws InvalidUserNameException invalid user name
     * @throws NotEnoughUserArgumentsException missing the username or real name
     * @throws RepeatedRegisterException have registered before
     */
    public void register(String arguments) throws InvalidUserNameException,
            NotEnoughUserArgumentsException, RepeatedRegisterException {
        if (userName != null) {
            throw new RepeatedRegisterException("You are already registered");
        }
        Pattern userPattern = Pattern.compile("^([\\S]+)\\s0\\s\\*\\s:(.*)");
        Matcher userMatcher = userPattern.matcher(arguments);
        String userName, realName;

        if (userMatcher.matches()) {
            userName = userMatcher.group(1);
            realName = userMatcher.group(2);
        } else {
            throw new NotEnoughUserArgumentsException("Not enough arguments");
        }
        setUserName(userName);
        setRealName(realName);
    }

    private void setUserName(String userName) throws InvalidUserNameException {
        Pattern userNamePattern = Pattern.compile("\\s");
        Matcher userNameMatcher = userNamePattern.matcher(userName);
        if (userNameMatcher.matches()) {
            throw new InvalidUserNameException("Invalid arguments to USER command");
        } else {
            this.userName = userName;
        }
    }

    private void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * checks whether the user has registered.
     * @return the result of check
     */
    public boolean isRegister() {
        boolean res = true;
        if (nickName.equals("*") || userName == null || realName == null) {
            res = false;
        }
        return res;
    }

    /**
     * sends error line to user when an exception caught by server.
     * @param message the error line content
     */
    public void sendError(String message) {
        String errorLine = ":" + serverName + " " + Configuration.ERROR_CODE + " " + nickName + " :" + message;
        send(errorLine);
    }

    /**
     * sends message to this user.
     * @param line the message content
     */
    public void send(String line) {
        pw.println(line);
        pw.flush();
    }

    public String getNickName() {
        return nickName;
    }

    /**
     * gets the server name.
     * @return server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * gets all channels in the server.
     * @return all channels in the server
     */
    public ConcurrentHashMap<String, ArrayList<String>> getChannels() {
        return channels;
    }

    /**
     * gets all registered users in the server.
     * @return all registered users in the server
     */
    public ConcurrentHashMap<String, ConnectionHandler> getRegisteredUsers() {
        return registeredUsers;
    }

    private void cleanup() {
        System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
        try {
            br.close();
            is.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
        }
    }
}
