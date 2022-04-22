package utils;

import api.Connection;
import com.google.gson.Gson;
import com.google.protobuf.Any;
import configs.ApplicationConfig;
import configs.BrokerConfig;
import configs.ConsumerConfig;
import configs.ProducerConfig;
import messages.Ack;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

/***
 * Helper class
 * @author anchitbhatia
 */
public class Helper {
    private static final Logger LOGGER = LogManager.getLogger("Helper");

    /***
     * Method to parse arguments
     * @param args : cmd line arguments
     * @return ApplicationConfig object
     * @throws Exception if invalid type or args passed
     */
    public static ApplicationConfig parseArgs(String[] args) throws Exception {
        if (args.length != 4 || !args[0].equals(Constants.TYPE_FLAG) || !args[2].equals(Constants.CONFIG_FLAG)) {
            throw new Exception("INVALID ARGS");
        }
        String type = args[Constants.TYPE_INDEX];
        String file = args[Constants.FILE_INDEX];

        Type classType;
        switch (type) {
            case Constants.TYPE_PRODUCER -> classType = ProducerConfig.class;
            case Constants.TYPE_BROKER -> classType = BrokerConfig.class;
            case Constants.TYPE_CONSUMER -> classType = ConsumerConfig.class;
            default -> throw new Exception("INVALID TYPE");
        }
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Object config = gson.fromJson(reader, classType);
        return new ApplicationConfig(type, config);
    }

    public static Connection connectBroker(Node brokerNode) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            return new Connection(broker);
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public static boolean sendRequest(Connection conn, String topic, Any packet) throws ConnectionException, IOException {
        conn.send(packet.toByteArray());
//            Ack.AckMessage message = this.receiveAck();
        byte[] bytes =  conn.receive();
        Any response = Any.parseFrom(bytes);
        if (response.is(Ack.AckMessage.class)) {
            return response.unpack(Ack.AckMessage.class).getAccept();
        }
        conn.close();
        return false;
    }

    public static Connection connectAllBrokers(List<Node> allBrokers, String topic, Any packet, boolean sendRequestPacket) {
        boolean brokerFound = false;
        while (!brokerFound) {
            for (Node broker : allBrokers) {
                try {
                    LOGGER.debug("Trying connecting to " + broker.getHostName() + " : " + broker.getPort());
                    Connection brokerConnection = connectBroker(broker);
                    if (!sendRequestPacket) {
                        LOGGER.info("Able to connect to broker " + broker.getHostName() + " : " + broker.getPort());
                        brokerFound = true;
                        return brokerConnection;
                    }
                    if (sendRequest(brokerConnection, topic, packet)) {
                        LOGGER.info("Able to connect to broker " + broker.getHostName() + " : " + broker.getPort());
                        brokerFound = true;
                        return brokerConnection;
                    }
                } catch (ConnectionException | IOException ignored) {
                }
            }
        }

        LOGGER.info("No active broker found! Exiting.");
        System.exit(0);
        return null;
    }

    public static NodeDetails getNodeDetailsObj(Node node) {
        return NodeDetails.newBuilder().
                setHostName(node.getHostName()).
                setPort(node.getPort()).
                setId(node.getId()).
                build();
    }

    public static Node getNodeObj(NodeDetails node) {
        return new Node(node.getHostName(), node.getPort(), node.getId());
    }
}
