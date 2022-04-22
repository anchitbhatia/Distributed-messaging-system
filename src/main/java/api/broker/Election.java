package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import messages.Election.ElectionInitiate;
import messages.Election.VictoryMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.*;

import java.util.List;
import java.util.TimerTask;

public class Election {
    private static final Logger LOGGER = LogManager.getLogger("Election");
    private Broker broker;
    private final Scheduler scheduler;
    private List<Connection> connections;

    public Election(Broker broker) {
        this.broker = broker;
        this.scheduler = new Scheduler(Constants.ELEC_ACK_WAIT_TIME);
        this.connections = null;
    }

    private void sendMsgToAll(Any packet) {
        this.connections = this.broker.getHbConnections();
        for (Connection connection : this.connections) {
            try {
                connection.send(packet.toByteArray());
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    private void victory() {
        LOGGER.info("Sending victory message");
        NodeDetails thisNode = Helper.getNodeDetailsObj(broker.node);
        VictoryMessage victoryMsg = VictoryMessage.newBuilder().setLeader(thisNode).build();
        Any victoryPacket = Any.pack(victoryMsg);
        initiateSyncToAll();
        sendMsgToAll(victoryPacket);
        broker.changeState(new Leader(broker));
    }

    public void initiateElection() {
        LOGGER.info("Starting election");
        this.connections = this.broker.getHbConnections();
        NodeDetails node = Helper.getNodeDetailsObj(this.broker.node);
        ElectionInitiate initiateMsg = ElectionInitiate.newBuilder().setInitiator(node).build();
        Any packet = Any.pack(initiateMsg);
        boolean otherBrokersExist = false;
        for (Connection connection : this.connections) {
            if (connection.getNode().getId() < this.broker.node.getId()) {
                try {
                    connection.send(packet.toByteArray());
                    otherBrokersExist = true;
                } catch (ConnectionException e) {
                    LOGGER.error("Unable to send initiate message to " + connection.getNode().getId());
                    e.printStackTrace();
                }
            }
        }
        if (!otherBrokersExist) {
            victory();
            return;
        }
        this.scheduler.scheduleTask(new TimerTask() {
            @Override
            public void run() {
                victory();
            }
        });
    }

    public void stopElectionTask() {
        LOGGER.info("Stopping election");
        this.scheduler.cancelTask();
    }

    private void initiateSyncToAll() {
        LOGGER.info("Initiating sync to all");
        if (this.connections == null) {
            this.connections = this.broker.getHbConnections();
        }
        for (Connection connection : this.connections) {
            this.broker.initiateSync(connection, Constants.SYNC_RECEIVE);
        }
    }

//    private class ElectionWaitTask extends TimerTask {
//
//        @Override
//        public void run() {
//            NodeDetails thisNode = Helper.getNodeDetailsObj(broker.node);
//            VictoryMessage victoryMsg = VictoryMessage.newBuilder().setLeader(thisNode).build();
//            Any packet = Any.pack(victoryMsg);
//            sendMsgToAll(packet);
//            initiateSyncToAll();
//        }
//    }
}
