package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import utils.MyTimer;
import utils.Node;
import messages.ConsumerRecord;
import messages.Request;
import utils.ConnectionException;
import utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConsumerApi {
    private final Connection brokerConnection;
    private final String topic;
    private Long nextOffset;
    private final BlockingQueue<ByteString> queue;
    private boolean isTimedOut;

    public ConsumerApi(Node brokerNode, String topic, Long startPosition) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.brokerConnection = new Connection(broker);
            this.topic = topic;
            this.nextOffset = startPosition;
            this.queue = new LinkedBlockingQueue<>();
            this.isTimedOut = false;
            Thread fetchingThread = new Thread(new MessageFetcher(), "Message Fetcher");
            fetchingThread.start();
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public ByteString poll(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public void close() throws IOException {
        brokerConnection.close();
    }

    public void printQueue(){
        System.out.println("\nConsumer: queue length is " + queue.size());
    }

    private void requestBroker() throws IOException {
        System.out.println("\nConsumer: requesting broker, Topic: " + this.topic + ", Offset: " + this.nextOffset);
        Request.ConsumerRequest request = Request.ConsumerRequest.newBuilder().
                setTopic(this.topic).
                setOffset(this.nextOffset).
                build();
        Any packet = Any.pack(request);
        brokerConnection.send(packet.toByteArray());
    }

    public void timedOut(){
        this.isTimedOut = true;
    }

//    private ConsumerRecord.Message fetchBroker(){
//        try {
//            byte[] record = new byte[Constants.MAX_BYTES];;
//            int count;
//            System.out.println("\nConsumer: reading from broker");
//            Timer timerObj = MyTimer.startTimer(this);
//            int n = inputStream.available();
//            while(n==0 && !isTimedOut){
//                n = inputStream.available();
//            }
//            if (isTimedOut){
//                timerObj.cancel();
//                this.isTimedOut = false;
//                return null;
//            }
//            count = inputStream.read(record);
//            timerObj.cancel();
//            while (count < 0){
//                count = inputStream.read(record);
//            }
//            record = Arrays.copyOf(record, count);
//            return ConsumerRecord.Message.parseFrom(record);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private byte[] receive() {
//        byte[] buffer = null;
//        try {
//            int length = this.inputStream.readInt();
//            if(length > 0) {
//                buffer = new byte[length];
//                this.inputStream.readFully(buffer, 0, buffer.length);
//                System.out.println("\nConsumer: read " + length + " bytes");
//            }
//        } catch (EOFException ignored) {} //No more content available to read
//        catch (IOException exception) {
//            exception.printStackTrace();
//        }
//        return buffer;
//    }

    private ConsumerRecord.Message fetchBroker(){
        byte[] record = this.brokerConnection.receive();
        try {
            Any packet = Any.parseFrom(record);
            return packet.unpack(ConsumerRecord.Message.class);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class MessageFetcher implements Runnable{

        @Override
        public void run() {
            while(!brokerConnection.isClosed()){
                try {
                    Thread.sleep(1000);
                    requestBroker();
                    ConsumerRecord.Message record = fetchBroker();
                    if (record!=null){
                        ByteString data = record.getData();
                        if (data.size() != 0){
                            queue.add(data);
                            System.out.println("\nConsumer: received from broker, Offset: " + record.getOffset() + ", Data: " + data);
                            nextOffset += data.size();
//                            Thread.sleep(1000);
                        }
                        else{
                            System.out.println("\nConsumer: sleeping for 3 seconds");
                            Thread.sleep(3000);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
