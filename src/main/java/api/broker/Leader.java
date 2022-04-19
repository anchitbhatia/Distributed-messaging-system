package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Leader extends BrokerState{
    private static final Logger LOGGER = LogManager.getLogger(Leader.class);

    public Leader(Broker broker) {
        super(broker);
    }

    @Override
    void startBroker() {
    }

    @Override
    void handleFollowRequest(ClientHandler clientHandler, FollowerRequest request) throws IOException {
        NodeDetails follower = request.getNode();
        LOGGER.info("Follow request from " + follower.getId());
        Connection connection = clientHandler.connection;
        connection.setNodeFields(follower);
        this.broker.addMember(connection.getNode());
        int i = 1;
        while (!connection.isClosed()){
//            String msg = "Message " + i;
//            BrokerMessage record = BrokerMessage.newBuilder().
//                    setTopic("First").
//                    setData(ByteString.copyFrom(msg.getBytes())).
//                    setOffset(i).
//                    build();
//            Any packet = Any.pack(record);
//            LOGGER.debug("Sending to follower " + follower.getId() + " : " + msg);
//            try {
//                connection.send(packet.toByteArray());
//                i++;
//                Thread.sleep(2000);
//            } catch (ConnectionException e) {
//                connection.close();
//                return;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

    }

    @Override
    void handleLeaderDetails(messages.Leader.LeaderDetails leaderDetails) throws IOException {

    }

//    @Override
//    void handleHeartBeat(ClientHandler clientHandler, HeartBeatMessage message) {
//
//    }
}
