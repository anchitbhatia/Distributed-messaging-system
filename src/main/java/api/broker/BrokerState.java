package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.HeartBeat.HeartBeatMessage;
import messages.Leader.LeaderDetails;
import messages.Node.NodeDetails;
import messages.Producer.ProducerRequest;
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

    abstract void startBroker();

    abstract void handleProducerRequest(Connection connection, ProducerRequest request);

    abstract void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException;

    abstract void handleLeaderDetails(LeaderDetails leaderDetails) throws IOException;

}
