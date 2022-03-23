package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.ProducerRecord;
import messages.Request;
import utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Client implements Runnable {
    private final Connection connection;
    private String type;

    public Client(Connection connection) throws IOException {
        this.connection = connection;
        this.type = Constants.TYPE_NULL;
    }

//    private byte[] receive() {
//        System.out.println("\nBroker: reading from client");
//        byte[] data = new byte[Constants.MAX_BYTES];
//        int count;
//        try {
//
//            count = this.inputStream.read(data);
//            if (count == -1){
//                return null;
//            }
//            while (count < 0){
//                count = this.inputStream.read(data);
//            }
//            return Arrays.copyOf(data, count);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    private byte[] receive() {
//        byte[] buffer = null;
//        try {
//            int length = this.inputStream.readInt();
//            if (length > 0) {
//                buffer = new byte[length];
//                this.inputStream.readFully(buffer, 0, buffer.length);
//            }
//        } catch (EOFException ignored) {
//        } //No more content available to read
//        catch (IOException exception) {
//            exception.printStackTrace();
//        }
//        return buffer;
//    }

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
        DatabaseApi.addRecord(topic, data);
    }

    private void serveRequest(Request.ConsumerRequest request) throws IOException {
        String topic = request.getTopic();
        long offset = request.getOffset();
        System.out.println("\nBroker: consumer requested, Topic: " + topic + ", Offset: " + offset);
        byte[] data = DatabaseApi.getRecord(topic, offset);
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
                    DatabaseApi.printDb();
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
