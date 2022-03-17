package api;

public class Node{
    private final String hostName;
    private final int port;

    public Node(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return hostName + ":" + port;
    }
}