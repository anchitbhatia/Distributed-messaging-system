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

    /***
     * Method to add member to membership table
     * @param node : node object of the member to be added
     * @param conn : connection to be added
     * @param connType : type of the connection to be added
     */
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

    /***
     * Method to add member to membership table
     * @param node node object of the member to be added
     * @param connType connection to be added
     */
    protected synchronized void addMember(Node node, String connType) throws IOException {
        this.addMember(node, null, connType);
    }

    /***
     * Method to remove member from membership table
     * @param id : id of the broker to be removed
     */
    protected synchronized void removeMember(int id) {
        Node node = this.members.remove(id);
        if (node == null) {
            LOGGER.info("Removing broker " + id);
        }
        this.msgConnections.remove(id);
        this.hbConnections.remove(id);
        this.printMembers();
    }

    /***
     * Method to remove member from membership table
     * @param id : id of the broker to be removed
     * @param type : type of the connection of the member to be removed
     */
    protected synchronized void removeConnection(int id, String type) {
        if (Objects.equals(type, Constants.CONN_TYPE_MSG)) {
            this.msgConnections.remove(id);
        }
        else if (Objects.equals(type, Constants.CONN_TYPE_HB)) {
            this.hbConnections.remove(id);
        }
    }

    /***
     * Method to check member in membership table
     * @param node : node to be checked
     * @return true if node exists in membership table else false
     */
    protected boolean checkMember(Node node){
        return this.members.containsKey(node.getId());
    }

    /***
     * Method to get members in membership table
     * @return list of nodes
     */
    protected List<Node> getMembers(){
        return new CopyOnWriteArrayList<>(this.members.values());
    }

    /***
     * Method to get msg connections
     * @return list of connections
     */
    protected List<Connection> getMsgConnections() {
        return new CopyOnWriteArrayList<>(this.msgConnections.values());
    }

    /***
     * Method to get heartbeat connections
     * @return list of connections
     */
    protected List<Connection> getHbConnections() {
        return new CopyOnWriteArrayList<>(this.hbConnections.values());
    }

    /***
     * Method to print members
     */
    public void printMembers() {
        LOGGER.info("Members: " + this.members.keySet());
    }
}
