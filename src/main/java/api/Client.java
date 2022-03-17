package api;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.ProducerRecord;
import utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Client implements Runnable{
    private final Socket socket;
    private String type;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;


    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());
        this.type = null;
    }

    private byte[] receive() {
        System.out.println("\nBroker: reading from client");
        byte[] data = new byte[Constants.MAX_BYTES];
        int count;
        try {
            count = this.inputStream.read(data);
            if (count == -1){
                return null;
            }
            while (count < 0){
                count = this.inputStream.read(data);
            }
            return Arrays.copyOf(data, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setType(Any packet){
        if (packet.is(ProducerRecord.ProducerMessage.class)){
            this.type = Constants.TYPE_PRODUCER;
        }
    }

    @Override
    public void run() {
        System.out.println("\nBroker: connection established " + socket.getPort());
        while (!socket.isClosed()){
            byte[] message = receive();

            if(message != null){
                try {
                    Any packet = Any.parseFrom(message);
                    if (type==null){
                        setType(packet);
                    }
                    System.out.println("\nBroker: Received packet from " + type);
                    switch (this.type){
                        case Constants.TYPE_PRODUCER: DatabaseApi.addRecord(packet.unpack(ProducerRecord.ProducerMessage.class));
                    }
                    DatabaseApi.printDb();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("\nBroker: connection disconnected " + socket.getPort());
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
