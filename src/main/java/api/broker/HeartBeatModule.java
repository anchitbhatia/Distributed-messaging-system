package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import messages.HeartBeat.HeartBeatMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartBeatModule {
    private static final Logger LOGGER = LogManager.getLogger(HeartBeatModule.class);
    private final Broker broker;
    private final Thread heartBeatSender;
//    private final Thread heartBeatReceiver;
    private boolean isModuleRunning;
    private ConcurrentHashMap<Integer, Connection> heartBeatConnections;

    public HeartBeatModule(Broker broker) {
        this.broker = broker;
        this.heartBeatSender = new Thread(new Sender(), "HB Sender");
        this.isModuleRunning = false;
        this.heartBeatConnections = new ConcurrentHashMap<>();
    }

    public void startModule(){
        this.heartBeatSender.start();
        this.isModuleRunning = true;
    }

    public void stopModule(){
        this.isModuleRunning = false;
    }

    private class Sender implements Runnable {
        @Override
        public void run() {
            LOGGER.info("Starting Heartbeat module");
            while (isModuleRunning) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
            }

        }
    }

}
