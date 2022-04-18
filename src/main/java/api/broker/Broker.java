package api.broker;

import utils.Node;

import java.io.IOException;

public class Broker {
    protected BrokerState state;

    public Broker(Node node) throws IOException {
        this.state = new Leader(this, node);
    }

    public Broker(Node node, Node leader) throws IOException {
        this.state = new Follower(this, node, leader);
    }

    public void startBroker(){
        this.state.startBroker();
    }
}
