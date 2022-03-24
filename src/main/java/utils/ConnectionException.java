package utils;

/***
 * Custom exception class to raise exception in connection failure
 * @author anchitbhatia
 */
public class ConnectionException extends Exception{
    public ConnectionException(String message) {
        super(message);
    }
}
