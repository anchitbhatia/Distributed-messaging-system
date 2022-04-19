package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import messages.HeartBeat.HeartBeatMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Node;
import utils.Scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class HeartBeatModule {
    private static final Logger LOGGER = LogManager.getLogger(HeartBeatModule.class);
    private final Broker broker;
    private final ConcurrentHashMap<Integer, Connection> heartBeatConnections;
    private final ConcurrentHashMap<Integer, Long> heartBeatReceiveTimes;
    private final Scheduler scheduler;

    public HeartBeatModule(Broker broker) {
        this.broker = broker;
        this.heartBeatConnections = new ConcurrentHashMap<>();
        this.heartBeatReceiveTimes = new ConcurrentHashMap<>();
        this.scheduler = new Scheduler(Constants.HB_SCHEDULER_TIME);
    }

    public void startModule(){
        this.scheduler.scheduleTask(new HeartBeatSenderTask());
    }

    public void parseHeartBeat(HeartBeatMessage message) {
        int id = message.getNode().getId();
        long time = System.nanoTime();
        this.heartBeatReceiveTimes.put(id, time);
        LOGGER.info("Heartbeat received from " + id);
        LOGGER.info("Members received: " + message.getMembersList().size());
    }

    public ConcurrentHashMap<Integer, Long> getHeartBeatReceiveTimes(){
        return heartBeatReceiveTimes;
    }

    public void stopModule(){
        this.scheduler.cancelTask();
    }

    private class HeartBeatSenderTask extends TimerTask {
        @Override
        public void run() {
            LOGGER.info("Heartbeat sender task started");
            NodeDetails nodeDetails = messages.Node.NodeDetails.newBuilder().
                    setHostName(broker.node.getHostName()).
                    setPort(broker.node.getPort()).
                    setId(broker.node.getId()).
                    build();

            ArrayList<messages.Node.NodeDetails> allNodes = new ArrayList<>();

            ArrayList<Connection> connections = new ArrayList<>();

            ConcurrentHashMap<Integer, Node> members = broker.membership.getMembers();

            for (Map.Entry<Integer, Node> item : members.entrySet()) {
                Node node = item.getValue();
                messages.Node.NodeDetails memberNode = messages.Node.NodeDetails.newBuilder().
                        setHostName(node.getHostName()).
                        setPort(node.getPort()).
                        setId(node.getId()).
                        build();
                allNodes.add(memberNode);
                if (!heartBeatConnections.containsKey(node.getId())){
                    try {
                        heartBeatConnections.put(node.getId(), new Connection(node));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                connections.add(heartBeatConnections.get(node.getId()));
            }

                messages.Node.NodeDetails currentNode = messages.Node.NodeDetails.newBuilder().
                        setHostName(broker.node.getHostName()).
                        setPort(broker.node.getPort()).
                        setId(broker.node.getId()).
                        build();

            if (!allNodes.contains(currentNode)) {
                allNodes.add(currentNode);
            }

            HeartBeatMessage message =  HeartBeatMessage.newBuilder().
                    setNode(nodeDetails).
                    addAllMembers(allNodes).
                    build();

            Any packet;
            for (Connection conn: connections) {
                if (conn.getNode().equals(broker.node)){
                    LOGGER.debug("Skipping " + conn.getNode());
                    continue;
                }
                packet = Any.pack(message);
                try {
                    LOGGER.debug("Sending heartbeat to " + conn.getNode().getId());
                    conn.send(packet.toByteArray());
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
            scheduler.scheduleTask(new HeartBeatSenderTask());
        }
    }
}
