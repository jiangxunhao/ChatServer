import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.DisconnectedException;
import exception.NoChannelExistsException;
import exception.NotRegisterException;
import exception.NoUserExistsException;
import exception.InvalidChannelNameException;
import exception.InvalidNickNameException;
import exception.InvalidPrivmsgArgumentsException;
import exception.InvalidUserNameException;
import exception.NotEnoughUserArgumentsException;
import exception.RepeatedRegisterException;

/**
 * the class handle all the different commands from user.
 */
public class CommandHandler {

    private ConnectionHandler connectionHandler;
    private String arguments;
    private String serverName, nickName;

    /**
     * constructs a commandHandler.
     * @param arguments the arguments from user
     * @param connectionHandler the connection handler
     */
    public CommandHandler(String arguments, ConnectionHandler connectionHandler) {
        this.arguments = arguments;
        this.connectionHandler = connectionHandler;
        serverName = connectionHandler.getServerName();
        nickName = connectionHandler.getNickName();
    }

    /**
     * handle the NICK command.
     */
    public synchronized void handleNick() {
        try {
            connectionHandler.setNickName(arguments);
            connectionHandler.getRegisteredUsers().put(arguments, connectionHandler);
        } catch (InvalidNickNameException invalidNickNameException) {
            connectionHandler.sendError(invalidNickNameException.getMessage());
        }

    }

    /**
     * handle the USER command.
     */
    public void handleUser() {
        try {
            connectionHandler.register(arguments);
            String text = "Welcome to the IRC network, " + nickName;
            String line = ":" + serverName + " " + Configuration.NICK_CODE + " " + nickName + " :" + text;
            connectionHandler.send(line);
        } catch (InvalidUserNameException invalidUserNameException) {
            connectionHandler.sendError(invalidUserNameException.getMessage());
        } catch (NotEnoughUserArgumentsException notEnoughUserArgumentsException) {
            connectionHandler.sendError(notEnoughUserArgumentsException.getMessage());
        } catch (RepeatedRegisterException repeatedRegisterException) {
            connectionHandler.sendError(repeatedRegisterException.getMessage());
        }
    }

    /**
     * handle the QUIT command.
     * @throws DisconnectedException throws disconnection exception to server
     */
    public synchronized void handleQuit() throws DisconnectedException {
        if (connectionHandler.isRegister()) {
            String line = ":" + nickName + " " + Configuration.QUIT_STRING;
            connectionHandler.sendAllMessage(line);
            Iterator<String> iterator = connectionHandler.getChannels().keySet().iterator();
            while (iterator.hasNext()) {
                String channelName = iterator.next();
                connectionHandler.partChannel(channelName);
            }
            connectionHandler.getRegisteredUsers().remove(nickName);
        }
        throw new DisconnectedException(" ... client has closed the connection ... ");
    }

    /**
     * handles the JOIN command.
     */
    public synchronized void handleJoin() {
        try {
            if (!connectionHandler.isRegister()) {
                throw new NotRegisterException("You need to register first");
            } else if (connectionHandler.getChannels().containsKey(arguments)) {
                connectionHandler.joinChannel(arguments);
            } else {
                connectionHandler.createChannel(arguments);
            }
            String line = ":" + nickName + " " + Configuration.JOIN_STRING + " " + arguments;
            connectionHandler.sendChannelMessage(arguments, line);
        } catch (NotRegisterException notRegisterException) {
            connectionHandler.sendError(notRegisterException.getMessage());
        } catch (InvalidChannelNameException invalidChannelNameException) {
            connectionHandler.sendError(invalidChannelNameException.getMessage());
        }
    }

    /**
     * handled the PART command.
     */
    public synchronized void handlePart() {
        try {
            if (!connectionHandler.isRegister()) {
                throw new NotRegisterException("You need to register first");
            } else if (!connectionHandler.getChannels().containsKey(arguments)) {
                throw new NoChannelExistsException("No channel exists with that name");
            } else {
                String line = ":" + nickName + " " + Configuration.PART_STRING + " " + arguments;
                connectionHandler.sendChannelMessage(arguments, line);
                connectionHandler.partChannel(arguments);
            }
        } catch (NotRegisterException notRegisterException) {
            connectionHandler.sendError(notRegisterException.getMessage());
        } catch (NoChannelExistsException noChannelExistsException) {
            connectionHandler.sendError(noChannelExistsException.getMessage());
        }
    }

    /**
     * handles the PRIVMSG command.
     */
    public void handlePrivmsg() {
        try {
            if (!connectionHandler.isRegister()) {
                throw new NotRegisterException("You need to register first");
            } else {
                Pattern privmsgChannelPattern = Pattern.compile("^(#\\w+)\\s:(.*)");
                Pattern privmsgUserPattern = Pattern.compile("^([A-Za-z_][\\w]*)\\s:(.*)");
                Matcher privmsgChannelMatcher = privmsgChannelPattern.matcher(arguments);
                Matcher privmsgUserMatcher = privmsgUserPattern.matcher(arguments);

                if (privmsgUserMatcher.matches()) {
                    String targetNickName = privmsgUserMatcher.group(1);
                    String message = privmsgUserMatcher.group(2);
                    connectionHandler.sendUserPrivmsgMessage(targetNickName, message);
                } else if (privmsgChannelMatcher.matches()) {
                    String targetChannelName = privmsgChannelMatcher.group(1);
                    String message = privmsgChannelMatcher.group(2);
                    connectionHandler.sendChannelPrivmsgMessage(targetChannelName, message);
                } else {
                    throw new InvalidPrivmsgArgumentsException("Invalid arguments to PRIVMSG command");
                }
            }
        } catch (NotRegisterException notRegisterException) {
            connectionHandler.sendError(notRegisterException.getMessage());
        } catch (NoUserExistsException noUserExistsException) {
            connectionHandler.sendError(noUserExistsException.getMessage());
        } catch (NoChannelExistsException noChannelException) {
            connectionHandler.sendError(noChannelException.getMessage());
        } catch (InvalidPrivmsgArgumentsException invalidPrivmsgArgumentsException) {
            connectionHandler.sendError(invalidPrivmsgArgumentsException.getMessage());
        }
    }

    /**
     * handles the NAMES command.
     */
    public void handleNames() {
        try {
            if (!connectionHandler.isRegister()) {
                throw new NotRegisterException("You need to register first");
            } else if (!connectionHandler.getChannels().containsKey(arguments)) {
                throw new NoChannelExistsException("No channel exists with that name");
            } else {
                String replyLine = ":" + serverName + " " + Configuration.NAMES_CODE + " " + nickName + " = " + arguments + " :";
                Iterator<String> iterator = connectionHandler.getChannels().get(arguments).iterator();
                while (iterator.hasNext()) {
                    String nick = iterator.next();
                    if (iterator.hasNext()) {
                        replyLine += (nick + " ");
                    } else {
                        replyLine += nick;
                    }
                    connectionHandler.send(replyLine);
                }
            }
        } catch (NotRegisterException notRegisterException) {
            connectionHandler.sendError(notRegisterException.getMessage());
        } catch (NoChannelExistsException noChannelExistsException) {
            connectionHandler.sendError(noChannelExistsException.getMessage());
        }
    }

    /**
     * handle the LIST command.
     */
    public void handleList() {
        Iterator<String> iterator = connectionHandler.getChannels().keySet().iterator();
        while (iterator.hasNext()) {
            String channel = iterator.next();
            connectionHandler.send(":" + serverName + " " + Configuration.LIST_EACH_CODE + " " + nickName + " " + channel);
        }
        connectionHandler.send(":" + serverName + " " + Configuration.LIST_FINAL_CODE + " " + nickName + " :End of LIST");
    }

    /**
     * handle the TIME command.
     */
    public void handleTime() {
        String text = LocalDateTime.now().toString();
        String line = ":" + serverName + " " + Configuration.TIME_CODE + " " + nickName + " :" + text;
        connectionHandler.send(line);
    }

    /**
     * handles the INFO command.
     */
    public void handleInfo() {
        String text = "";
        String line = ":" + serverName + " " + Configuration.INFO_CODE + " " + nickName + " :" + text;
        connectionHandler.send(line);
    }

    /**
     * handles the PING command.
     */
    public void handlePing() {
        String text = arguments;
        String line = Configuration.PING_REPLY + " " + text;
        connectionHandler.send(line);
    }


}
