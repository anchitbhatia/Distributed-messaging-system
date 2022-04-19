package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.HeartBeat.HeartBeatMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.io.IOException;

public abstract class BrokerState {
    private static final Logger LOGGER = LogManager.getLogger(BrokerState.class);
    protected Broker broker;

    public BrokerState(Broker broker) {
        this.broker = broker;
    }

    protected void newMember(Node node) {
        this.broker.membership.addMember(node);
    }

    protected void handleHeartBeat(ClientHandler clientHandler, HeartBeatMessage message) {
        clientHandler.heartBeatCount++;
        NodeDetails node = message.getNode();
        clientHandler.connection.setNodeFields(node);
        LOGGER.info("Heartbeat received from " + node.getId());
        LOGGER.info("Members received: " + message.getMembersList().size());
//        if (clientHandler.heartBeatCount > 10) {
//            this.broker.membership.replaceMembers(message.getMembersList());
//            clientHandler.heartBeatCount = 0;
//        }
    }

    abstract void startBroker();

    abstract void handleFollowRequest(ClientHandler clientHandler, FollowerRequest request) throws IOException;

}
