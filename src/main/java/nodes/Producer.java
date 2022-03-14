package nodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Producer extends Node{
    private Node brokerNode;
    private DataOutputStream outputStream;

    public Producer(String hostname, int port, Node brokerNode) {
        super(hostname, port);
        this.brokerNode = brokerNode;
        try {
            this.initiateConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void initiateConnection() throws IOException {
        Socket broker = new Socket(brokerNode.getHostname(), brokerNode.getPort());
        this.outputStream = new DataOutputStream(broker.getOutputStream());
    }

    public void publish(String topic, byte[] data){
        
    }
}