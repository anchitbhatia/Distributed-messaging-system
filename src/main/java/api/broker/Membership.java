package api.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Membership {
    private static final Logger LOGGER = LogManager.getLogger(Membership.class);
    private final ConcurrentHashMap<Integer, Node> members;

    public Membership() {
        this.members = new ConcurrentHashMap<>();
    }

    protected void addMember(Node node){
        if (!checkMember(node.getId())) {
            members.put(node.getId(), node);
        }
        LOGGER.debug("New member added " + node);
        printMembers();
    }

    public void removeMember(Integer id){
        members.remove(id);
    }

    public boolean checkMember(Integer id){
        return members.contains(id);
    }

    public ConcurrentHashMap<Integer, Node> getMembers(){
        return members;
    }

    private void printMembers() {
        LOGGER.info(getMembers());
    }
}
