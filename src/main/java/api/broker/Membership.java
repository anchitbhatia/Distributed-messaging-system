package api.broker;

import api.Connection;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Node;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Membership {
    private static final Logger LOGGER = LogManager.getLogger("MSmodule");
    private final Broker broker;

    private final ConcurrentHashMap<Integer, Node> members;
    private final ConcurrentHashMap<Integer, Connection> msgConnections;
    private final ConcurrentHashMap<Integer, Connection> hbConnections;


    public Membership(Broker broker) {
        this.broker = broker;
        this.members = new ConcurrentHashMap<>();
        this.msgConnections = new ConcurrentHashMap<>();
        this.hbConnections = new ConcurrentHashMap<>();
    }

//    protected synchronized void replaceMembers(List<NodeDetails> membersList) {
//        LOGGER.info("Replacing members list");
//        ConcurrentHashMap<Integer, Node> newMembers = new ConcurrentHashMap<>();
//        for (NodeDetails item: membersList) {
//            if (!newMembers.containsKey(item.getId())) {
//                Node node = new Node(item.getHostName(), item.getPort(), item.getId());
//                newMembers.put(item.getId(), node);
//            }
//        }
//        members = newMembers;
//        LOGGER.info("New members list " + members);
//    }

    protected synchronized void addMember(Node node, Connection conn, String connType) {
        if (!checkMember(node)) {
            members.put(node.getId(), node);
            if (Objects.equals(connType, Constants.CONN_TYPE_MSG)) {
                msgConnections.put(node.getId(), conn);
            } else if (Objects.equals(connType, Constants.CONN_TYPE_HB)) {
                hbConnections.put(node.getId(), conn);
            }
        }
        LOGGER.debug("New member added " + node);
        printMembers();
    }

    public void removeMember(Node node){
        removeMember(node.getId());
    }

    protected synchronized void removeMember(int id) {
        LOGGER.info("Removing broker " + id);
        members.remove(id);
        msgConnections.remove(id);
        hbConnections.remove(id);
    }

    private boolean checkMember(Node node){
        return members.containsKey(node.getId());
    }

    protected ConcurrentHashMap<Integer, Node> getMembers(){
        return members;
    }

    private void printMembers() {
        LOGGER.info(getMembers().keySet());
    }
}
