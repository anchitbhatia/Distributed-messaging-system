package api;

import utils.Node;
import messages.ConsumerRecord;
import messages.Request;
import utils.ConnectionException;
import utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsumerApi {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Socket broker;
    private final String topic;
    private int nextOffset;
    private final Thread fetchingThread;
    private final ConcurrentLinkedQueue<byte[]> queue;

    public ConsumerApi(Node brokerNode, String topic, int startPosition) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.broker = broker;
            this.inputStream = new DataInputStream(broker.getInputStream());
            this.outputStream = new DataOutputStream(broker.getOutputStream());
            this.topic = topic;
            this.nextOffset = startPosition;
            this.queue = new ConcurrentLinkedQueue<>();
            this.fetchingThread = new Thread(new MessageFetcher());
            this.fetchingThread.start();
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void close() throws IOException {
        broker.close();
    }

    private void requestBroker() throws IOException {
        Request.ConsumerRequest request = Request.ConsumerRequest.newBuilder().
                setTopic(this.topic).
                setOffset(this.nextOffset).
                build();
        this.outputStream.write(request.toByteArray());
    }

    private ConsumerRecord.Message fetchBroker(){
        try {
            byte[] record;
            int count;
            record = new byte[Constants.MAX_BYTES];
            count = inputStream.read(record);
            while (count < 0){
                count = inputStream.read(record);
            }
            return ConsumerRecord.Message.parseFrom(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class MessageFetcher implements Runnable{

        @Override
        public void run() {
            while(broker.isConnected()){
                try {
                    requestBroker();
                    ConsumerRecord.Message record = fetchBroker();
                    if (record!=null){
                        byte[] data = record.getData().toByteArray();
                        queue.add(data);
                        nextOffset += data.length;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
