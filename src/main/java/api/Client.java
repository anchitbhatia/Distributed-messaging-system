package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.ProducerRecord;
import messages.Request;
import utils.Constants;

import java.io.IOException;

public class Client implements Runnable {
    private final Connection connection;
    private String type;

    public Client(Connection connection) throws IOException {
        this.connection = connection;
        this.type = Constants.TYPE_NULL;
    }

    private void setType(Any packet) {
        if (packet.is(ProducerRecord.ProducerMessage.class)) {
            this.type = Constants.TYPE_PRODUCER;
        } else if (packet.is(Request.ConsumerRequest.class)) {
            this.type = Constants.TYPE_CONSUMER;
        } else {
            this.type = Constants.TYPE_NULL;
        }
    }

    private void newRecord(ProducerRecord.ProducerMessage record) {
        String topic = record.getTopic();
        byte[] data = record.getData().toByteArray();
        System.out.println("\nBroker: received from Producer, Topic: " + topic + ", Data: " + record.getData());
        Database.addRecord(topic, data);
    }

    private void serveRequest(Request.ConsumerRequest request) throws IOException {
        String topic = request.getTopic();
        long offset = request.getOffset();
        System.out.println("\nBroker: consumer requested, Topic: " + topic + ", Offset: " + offset);
        byte[] data = Database.getRecord(topic, offset);
        ConsumerRecord.Message record;
        if (data != null) {
            record = ConsumerRecord.Message.newBuilder().
                    setOffset(offset).
                    setData(ByteString.copyFrom(data)).
                    build();
            System.out.println("\nBroker: responding to consumer, Topic: " + topic + ", Data: " + ByteString.copyFrom(data));
        } else {
            data = new byte[0];
            record = ConsumerRecord.Message.newBuilder().
                    setOffset(offset).
                    setData(ByteString.copyFrom(data))
                    .build();
            System.out.println("\nBroker: responding to consumer, Topic: " + topic + ", Data: null");
        }
        Any packet = Any.pack(record);
        connection.send(packet.toByteArray());
    }

    @Override
    public void run() {
        System.out.println("\nBroker: connection established " + connection);
        while (!connection.isClosed()) {
            byte[] message =  connection.receive();
            if (message != null) {
                try {
                    Any packet = Any.parseFrom(message);
                    if (type == null) {
                        setType(packet);
                    }
                    System.out.println("\nBroker: received packet from " + type);
                    switch (this.type) {
                        case Constants.TYPE_PRODUCER -> newRecord(packet.unpack(ProducerRecord.ProducerMessage.class));
                        case Constants.TYPE_CONSUMER -> serveRequest(packet.unpack(Request.ConsumerRequest.class));
                        default -> System.out.println("\nBroker: invalid client");
                    }
                    Database.printDb();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("\nBroker: connection disconnected " + connection);
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
