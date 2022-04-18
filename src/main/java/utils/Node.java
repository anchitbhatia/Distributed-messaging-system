package utils;

import java.util.Objects;

/***
 * Node class to manage node
 * @author anchitbhatia
 */
public class Node{
    private final String hostName;
    private int port;
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

    public void setPort(int port) {
        this.port = port;
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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        Node node = (Node) obj;
        return Objects.equals(this.getHostName(), node.getHostName()) &&
                Objects.equals(this.getPort(), node.getPort()) &&
                Objects.equals(this.getId(), node.getId()) ;
    }
}