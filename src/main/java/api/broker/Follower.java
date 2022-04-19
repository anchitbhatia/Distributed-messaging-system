package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.BrokerRecord.BrokerMessage;
import messages.Follower.FollowerRequest;
import messages.Leader.LeaderDetails;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;

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

    private void restartClientThread(){
        this.clientThread.stop();
    }

    @Override
    void handleFollowRequest(ClientHandler clientHandler, messages.Follower.FollowerRequest request) throws IOException {
        LeaderDetails leader = LeaderDetails.newBuilder().
                setHostName(this.broker.leader.getHostName()).
                setPort(this.broker.leader.getPort()).
                setId(this.broker.leader.getId()).
                build();
        Any packet = Any.pack(leader);
        try {
            clientHandler.connection.send(packet.toByteArray());
            LOGGER.info("Sending leader details to " + request.getNode());
        } catch (ConnectionException ignored) {
        } finally {
            this.broker.addMember(clientHandler.connection.getNode());
            clientHandler.connection.close();
        }
    }

    @Override
    void handleLeaderDetails(LeaderDetails leaderDetails) throws IOException {
        Node newLeader = new Node(leaderDetails.getHostName(), leaderDetails.getPort(), leaderDetails.getId());
        LOGGER.info("Received details of leader " + newLeader.getId());
        this.broker.setNewLeader(newLeader);
        this.broker.changeState(new Follower(this.broker));
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

    private Any fetchLeader() throws InvalidProtocolBufferException {
        if (!this.leaderConnection.isClosed()) {
            byte[] record = this.leaderConnection.receive();
            return Any.parseFrom(record);
        }
        return null;
    }

    private void close() throws IOException {
        LOGGER.info("Closing connection to broker with id " + this.broker.leader.getId());
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
            while (!leaderConnection.isClosed()) {
                Any record = null;
                try {
                    record = fetchLeader();
                    if (record != null) {
                        if (record.is(BrokerMessage.class)) {
                            ByteString data = record.unpack(BrokerMessage.class).getData();
                            if (data.size() != 0) {
                                LOGGER.info("Received data: " + data.toStringUtf8());
                            } else {
                                Thread.sleep(1000);
                            }
                        } else if (record.is(LeaderDetails.class)) {
                            close();
                            handleLeaderDetails(record.unpack(LeaderDetails.class));
                        }
                    }
                    else{
                        close();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
