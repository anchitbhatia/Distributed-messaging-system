package nodes;

public class Node{
    private final String hostname;
    private final int port;

    public Node(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}