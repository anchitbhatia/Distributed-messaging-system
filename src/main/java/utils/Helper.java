package utils;

import com.google.gson.Gson;
import configs.ApplicationConfig;
import configs.BrokerConfig;
import configs.ConsumerConfig;
import configs.ProducerConfig;
import messages.Node.NodeDetails;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;

/***
 * Helper class
 * @author anchitbhatia
 */
public class Helper {

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
