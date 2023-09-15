/**
 * This is the configuration for server which contains command and reply code.
 */
public abstract class Configuration {
    /**
     * The NICK command.
     */
    public static final String NICK_STRING = "NICK";
    /**
     * The USER command.
     */
    public static final String USER_STRING = "USER";
    /**
     * The QUIT command.
     */
    public static final String QUIT_STRING = "QUIT";
    /**
     * The JOIN command.
     */
    public static final String JOIN_STRING = "JOIN";
    /**
     * The PART command.
     */
    public static final String PART_STRING = "PART";
    /**
     * The NAMES command.
     */
    public static final String NAMES_STRING = "NAMES";
    /**
     * The LIST command.
     */
    public static final String LIST_STRING = "LIST";
    /**
     * The PRIVMSG command.
     */
    public static final String PRIVMSG_STRING = "PRIVMSG";
    /**
     * The TIME command.
     */
    public static final String TIME_STRING = "TIME";
    /**
     * The INFO command.
     */
    public static final String INFO_STRING = "INFO";
    /**
     * The PING command.
     */
    public static final String PING_STRING = "PING";

    /**
     * The reply message for PING command.
     */
    public static final String PING_REPLY = "PONG";

    /**
     * The successful reply code of NICK command.
     */
    public static final String NICK_CODE = "001";
    /**
     * The successful reply code of NAMES command.
     */
    public static final String NAMES_CODE = "353";
    /**
     * The successful reply code of LIST command in each line.
     */
    public static final String LIST_EACH_CODE = "322";
    /**
     * The successful reply code of LIST command in the last line.
     */
    public static final String LIST_FINAL_CODE = "323";
    /**
     * The successful reply code of TIME command.
     */
    public static final String TIME_CODE = "391";
    /**
     * The successful reply code of INFO command.
     */
    public static final String INFO_CODE = "371";
    /**
     * The reply code of ERROR.
     */
    public static final String ERROR_CODE = "400";



}
