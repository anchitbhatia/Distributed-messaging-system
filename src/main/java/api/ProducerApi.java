package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import utils.Node;
import messages.ProducerRecord;
import utils.ConnectionException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ProducerApi {
    private final DataOutputStream outputStream;
    private final Socket broker;

    public ProducerApi(Node brokerNode) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.broker = broker;
            this.outputStream = new DataOutputStream(broker.getOutputStream());
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void send(String topic, byte[] data) throws IOException {
        System.out.println("\nPublisher: publishing Topic: " + topic + ", Length: " + data.length);
        ProducerRecord.ProducerMessage msg = ProducerRecord.ProducerMessage.newBuilder().setTopic(topic).setData(ByteString.copyFrom(data)).build();
        Any packet = Any.pack(msg);
        byte[] packetBytes = packet.toByteArray();
        this.outputStream.writeInt(packetBytes.length);
        this.outputStream.write(packetBytes);
    }

    public void close() throws IOException {
        System.out.println("\nPublisher: closing connection to broker at " + broker.getPort());
        broker.close();
    }
}