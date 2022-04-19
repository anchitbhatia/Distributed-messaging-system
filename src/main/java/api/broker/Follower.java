package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.BrokerRecord.BrokerMessage;
import messages.Follower.FollowerRequest;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;

import java.io.IOException;

public class Follower extends BrokerState{
    private static final Logger LOGGER = LogManager.getLogger(Follower.class);
    private Connection leaderConnection;
    private Thread clientThread;

    public Follower(Broker broker) throws IOException {
        super(broker);
        this.leaderConnection = null;
        this.clientThread = new Thread(new ClientThread(), "client");
    }

    public void startBroker(){
        LOGGER.info("Starting clientThread");
        this.clientThread.start();
    }

    @Override
    void handleFollowRequest(ClientHandler clientHandler, messages.Follower.FollowerRequest request) throws IOException {

    }

//    @Override
//    void handleHeartBeat(ClientHandler clientHandler, HeartBeat.HeartBeatMessage message) {
//
//    }

    private void connectLeader() throws IOException {
        this.leaderConnection = new Connection(this.broker.leader);
        NodeDetails follower = messages.Node.NodeDetails.newBuilder().
                setHostName(this.broker.node.getHostName()).
                setPort(this.broker.node.getPort()).
                setId(this.broker.node.getId()).
                build();
        FollowerRequest request = FollowerRequest.newBuilder().
                setNode(follower).
                build();
        Any packet = Any.pack(request);
        try {
            this.leaderConnection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            this.close();
        }
    }

    private BrokerMessage fetchLeader() throws IOException, ConnectionException {
        if (!this.leaderConnection.isClosed()) {
            byte[] record = this.leaderConnection.receive();
            try {
                Any packet = Any.parseFrom(record);
                return packet.unpack(BrokerMessage.class);
            } catch (NullPointerException e) {
                this.close();
                throw new ConnectionException("Connection closed!");
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void close() throws IOException {
        LOGGER.info("Closing connection to leader with id " + this.broker.leader.getId());
        this.leaderConnection.close();
    }

    private class ClientThread implements Runnable{

        @Override
        public void run() {
            LOGGER.debug("Data thread started");
            try {
                connectLeader();
                broker.addMember(broker.leader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!leaderConnection.isClosed()){
                BrokerMessage record = null;
                try {
                    record = fetchLeader();
                    if (record!=null){
                        ByteString data = record.getData();
                        if (data.size() != 0){
                            LOGGER.info("Received data: " + data.toStringUtf8());
                        }
                        else{
                            Thread.sleep(1000);
                        }
                    }
                }catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    try {
                        close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
