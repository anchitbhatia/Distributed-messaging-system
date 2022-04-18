package api.broker;

import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Membership {
    private static final Logger LOGGER = LogManager.getLogger(Membership.class);
//    private final ConcurrentHashMap<Integer, Connection> heartBeatConnections;
    private ConcurrentHashMap<Integer, Node> members;

//    private ConcurrentLinkedDeque<Node> members;


    public Membership() {
        this.members = new ConcurrentHashMap<>();
    }

    protected synchronized void replaceMembers(List<NodeDetails> membersList) {
        LOGGER.info("Replacing members list");
        ConcurrentHashMap<Integer, Node> newMembers = new ConcurrentHashMap<>();
        for (NodeDetails item: membersList) {
            if (!newMembers.containsKey(item.getId())) {
                Node node = new Node(item.getHostName(), item.getPort(), item.getId());
                newMembers.put(item.getId(), node);
            }
        }
        members = newMembers;
        LOGGER.info("New members list " + members);
    }

    protected synchronized void addMember(Node node) {

        if (!checkMember(node)) {
            members.put(node.getId(), node);
        }
        LOGGER.debug("New member added " + node);
        printMembers();
    }

    public synchronized void removeMember(Node node){
        members.remove(node.getId());
    }

    public boolean checkMember(Node node){
        return members.containsKey(node.getId());
    }


    public ConcurrentHashMap<Integer, Node> getMembers(){
        return  members;
    }

//    public ArrayList<Node> getAllNodes(){
//        ConcurrentHashMap<Node, Node> allMembers = getMembers();
//        return new ArrayList<>(allMembers.values());
//    }

    private void printMembers() {
        LOGGER.info(getMembers());
    }
}
