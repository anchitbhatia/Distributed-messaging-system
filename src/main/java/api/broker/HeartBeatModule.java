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
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class HeartBeatModule {
    private static final Logger LOGGER = LogManager.getLogger("HBmodule");
    private final Broker broker;
    private final ConcurrentHashMap<Integer, Long> heartBeatReceiveTimes;
    private final List<Integer> recentlyRemoved;
    private final Scheduler scheduler;

    public HeartBeatModule(Broker broker) {
        LOGGER.debug("Initializing Heartbeat module");
        this.broker = broker;
        this.heartBeatReceiveTimes = new ConcurrentHashMap<>();
        this.recentlyRemoved = new ArrayList<>();
        this.scheduler = new Scheduler(Constants.HB_SCHEDULER_TIME);
    }

    /***
     * Method to remove member from heartbeatreceivetimes tab;e
     * @param id
     */
    protected synchronized void removeMember(int id) {
        LOGGER.warn("Removing heart beat receive times " + id);
        this.recentlyRemoved.add(id);
        this.heartBeatReceiveTimes.remove(id);
    }

    public void startModule(){
        LOGGER.debug("Starting Heartbeat module");
        this.scheduler.scheduleTask(new HeartBeatSenderTask());
    }

    public void parseHeartBeat(HeartBeatMessage message) {
        int id = message.getNode().getId();
        long time = System.nanoTime();
        this.heartBeatReceiveTimes.put(id, time);
        List<NodeDetails> membersReceived = message.getMembersList();
        List<Node> nodes = this.broker.getMembers();
        List<Integer> ids = new ArrayList<>();
        for (Node node: nodes) {
            ids.add(node.getId());
        }
        LOGGER.info("Current members: " + ids);
        LOGGER.info("Received heartbeat from " + id + ", membersReceived: " + membersReceived.size());
        for (NodeDetails member: membersReceived) {
            Node node = new Node(member.getHostName(), member.getPort(), member.getId());
            if ((this.broker. node.getId() != node.getId()) && !(this.recentlyRemoved.contains(node.getId()))) {
                this.broker.addMember(node, Constants.CONN_TYPE_HB);
            }
        }
        nodes = this.broker.getMembers();
        ids = new ArrayList<>();
        for (Node node: nodes) {
            ids.add(node.getId());
        }
        LOGGER.info("Current members: " + ids);
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
            NodeDetails nodeDetails = messages.Node.NodeDetails.newBuilder().
                    setHostName(broker.node.getHostName()).
                    setPort(broker.node.getPort()).
                    setId(broker.node.getId()).
                    build();

            ArrayList<messages.Node.NodeDetails> allNodes = new ArrayList<>();

            List<Node> members = broker.getMembers();

            for (Node node : members) {
                messages.Node.NodeDetails memberNode = messages.Node.NodeDetails.newBuilder().
                        setHostName(node.getHostName()).
                        setPort(node.getPort()).
                        setId(node.getId()).
                        build();
                allNodes.add(memberNode);
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

            List<Connection> hbConnections = broker.getHbConnections();

            for (Connection conn: hbConnections) {
                if (conn.getNode().equals(broker.node)){
                    LOGGER.debug("Skipping " + conn.getNode());
                    continue;
                }
                Any packet = Any.pack(message);
                try {
//                    LOGGER.debug("Sending heartbeat to " + conn.getNode().getId());
                    conn.send(packet.toByteArray());
                } catch (ConnectionException e) {
//                    LOGGER.error("Connection closed");
//                    broker.removeMember(conn.getNode().getId());
                }
            }
            scheduler.scheduleTask(new HeartBeatSenderTask());
        }
    }
}
