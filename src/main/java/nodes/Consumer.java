package nodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Consumer {
    private final DataInputStream inputStream;
    private final Socket broker;
    private final String topic;
    private int currentPosition;

    public Consumer(Node brokerNode, String topic, int startPosition) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostname(), brokerNode.getPort());
            this.broker = broker;
            this.inputStream = new DataInputStream(broker.getInputStream());
            this.topic = topic;
            this.currentPosition = startPosition;
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void close() throws IOException {
        broker.close();
    }
}
