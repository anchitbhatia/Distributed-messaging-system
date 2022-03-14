package nodes;

import com.google.protobuf.ByteString;
import messages.ProducerMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Producer{
    private final DataOutputStream outputStream;
    private final Socket broker;

    public Producer(Node brokerNode) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostname(), brokerNode.getPort());
            this.broker = broker;
            this.outputStream = new DataOutputStream(broker.getOutputStream());
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void send(String topic, byte[] data) throws IOException {
        ProducerMessage.Message msg = ProducerMessage.Message.newBuilder().setTopic(topic).setData(ByteString.copyFrom(data)).build();
        this.outputStream.write(msg.toByteArray());
    }

    public void close() throws IOException {
        broker.close();
    }
}