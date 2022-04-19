package utils;

/***
 * Constants class to manage constants
 * @author anchitbhatia
 */
public class Constants {
    public static final String TYPE_FLAG = "-type";
    public static final int TYPE_INDEX = 1;
    public static final String CONFIG_FLAG = "-config";
    public static final int FILE_INDEX = 3;

    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_PRODUCER = "producer";
    public static final String TYPE_BROKER = "broker";
    public static final String TYPE_CONSUMER = "consumer";
    public static final String TYPE_SUBSCRIBER = "subscriber";
    public static final String TYPE_FOLLOWER = "follower";
    public static final String TYPE_LEADER = "leader";
    public static final String TYPE_HEARTBEAT = "heartbeat";
    public static final String TYPE_NULL = null;
    public static final String PUSH_TYPE = "push";

    public static final String CONN_TYPE_MSG = "msg";
    public static final String CONN_TYPE_HB = "msg";

    public static final int POLL_TIMEOUT = 2000;
    public static final int HB_SCHEDULER_TIME = 2000;
    public static final int FD_SCHEDULER_TIME = 5000;
    public static final long FAILURE_TIMEOUT = 10000;
}
