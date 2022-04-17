package utils;

/***
 * Node class to manage node
 * @author anchitbhatia
 */
public class Node{
    private final String hostName;
    private final int port;
    private int id;

    public Node(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public Node(String hostName, int port, int id) {
        this.hostName = hostName;
        this.port = port;
        this.id = id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + ":" + hostName + ":" + port;
    }
}