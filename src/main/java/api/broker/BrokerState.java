package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.Leader.LeaderDetails;
import messages.Producer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/***
 * Abstract class to manage broker state
 * @author anchitbhatia
 */
public abstract class BrokerState {
    private static final Logger LOGGER = LogManager.getLogger(BrokerState.class);
    protected Broker broker;

    public BrokerState(Broker broker) {
        this.broker = broker;
    }

    abstract void startBroker();

    abstract void handleProducerRequest(Connection connection, Producer.ProducerRequest request);

    abstract void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException;

    abstract void handleLeaderDetails(LeaderDetails leaderDetails) throws IOException;

}
