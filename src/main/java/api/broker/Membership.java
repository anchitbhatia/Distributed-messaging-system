package api.broker;

import api.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
            LOGGER.debug("New member added " + node);
            printMembers();
        }
        try {
            if (Objects.equals(connType, Constants.CONN_TYPE_MSG) && !msgConnections.containsKey(node.getId())) {
                if (conn == null) {
                    conn = new Connection(node);
                }
                msgConnections.put(node.getId(), conn);
                LOGGER.debug("New msg connection added " + node);
            } else if (Objects.equals(connType, Constants.CONN_TYPE_HB) && !hbConnections.containsKey(node.getId())) {
                if (conn == null) {
                    conn = new Connection(node);
                }
                hbConnections.put(node.getId(), conn);
                LOGGER.debug("New hb connection added " + node);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to create connection " + connType);
            removeMember(node.getId());
        }
    }

    protected synchronized void addMember(Node node, String connType) throws IOException {
        this.addMember(node, null, connType);
    }

//    public void removeMember(Node node){
//        removeMember(node.getId());
//    }

    protected synchronized void removeMember(int id) {
        Node node = this.members.remove(id);
        if (node == null) {
            LOGGER.info("Removing broker " + id);
        }
        this.msgConnections.remove(id);
        this.hbConnections.remove(id);
        this.printMembers();
    }

    protected synchronized void removeConnection(int id, String type) {
        if (Objects.equals(type, Constants.CONN_TYPE_MSG)) {
            this.msgConnections.remove(id);
        }
        else if (Objects.equals(type, Constants.CONN_TYPE_HB)) {
            this.hbConnections.remove(id);
        }
    }

    private boolean checkMember(Node node){
        return this.members.containsKey(node.getId());
    }

    protected List<Node> getMembers(){
        return new CopyOnWriteArrayList<>(this.members.values());
    }

    protected List<Connection> getMsgConnections() {
        return new CopyOnWriteArrayList<>(this.msgConnections.values());
    }

    protected List<Connection> getHbConnections() {
        return new CopyOnWriteArrayList<>(this.hbConnections.values());
    }

    private void printMembers() {
        LOGGER.info("Members: " + this.members.keySet());
    }
}
