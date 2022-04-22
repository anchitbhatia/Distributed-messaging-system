import api.broker.*;
import org.junit.jupiter.api.BeforeAll;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UnitTest {
    public static Broker broker;
    private static ArrayList<Node> nodeArray;
    private static boolean isTesting;


    @BeforeAll
    static void initialize() {
        Node brokerNode = new Node("localhost", 5555, 1);
        try {
            broker = new Broker(brokerNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nodeArray = new ArrayList();
        for (int i = 10; i < 16; i++){
            int port = 3000 + i;
            int id = i;
            Node node = new Node("localhost", port, id);
            Thread newNode = new Thread(() -> {
                while(isTesting) {
                    try {
                        ServerSocket server = new ServerSocket(port);
                        Socket client= server.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            newNode.start();
            nodeArray.add(node);
        }
    }
    @Test
    void addMemberCheckHB(){
        broker.addMember(nodeArray.get(0), Constants.CONN_TYPE_HB);
        System.out.println(nodeArray.get(0));
        broker.printMembers();
        assertTrue(broker.checkMember(nodeArray.get(0)));
    }

    @Test
    void addMemberCheckMSG(){
        broker.addMember(nodeArray.get(1), Constants.CONN_TYPE_MSG);
        assertTrue(broker.checkMember(nodeArray.get(1)));
    }

    @Test
    void removeMemberCheckHB(){
        broker.addMember(nodeArray.get(2), Constants.CONN_TYPE_HB);
        assertTrue(broker.checkMember(nodeArray.get(2)));
        broker.removeMember(1);
        assertFalse(broker.checkMember(nodeArray.get(2)));
    }

    @Test
    void removeMemberCheckMSG(){
        broker.addMember(nodeArray.get(3), Constants.CONN_TYPE_MSG);
        broker.removeMember(2);
        assertFalse(broker.checkMember(nodeArray.get(3)));
    }

    @Test
    void getMembersCheckTrue(){
        ArrayList<Node> nodeArrayCheck = new ArrayList<>();
        nodeArrayCheck.add(nodeArray.get(0));
        nodeArrayCheck.add(nodeArray.get(1));
        boolean ifEqual = nodeArrayCheck.equals(broker.getMembers());
        assertTrue(ifEqual);

    }

    @Test
    void getMembersCheckFalse(){
        ArrayList<Node> nodeArrayCheck = new ArrayList<>();
        nodeArrayCheck.add(nodeArray.get(0));
        nodeArrayCheck.add(nodeArray.get(2));
        boolean ifEqual = nodeArrayCheck.equals(broker.getMembers());
        assertFalse(ifEqual);
    }
}
