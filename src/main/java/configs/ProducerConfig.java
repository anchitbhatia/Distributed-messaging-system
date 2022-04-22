package configs;

import utils.Node;

import java.util.ArrayList;

/***
 * Class to parse Producer config
 * @author anchitbhatia
 */
public class ProducerConfig {
    private Node host;
    private ArrayList<Node> brokers;
    private String topic;
    private String file;

    public Node getHost() {
        return host;
    }

    public ArrayList<Node> getBrokers() {
        return brokers;
    }

    public String getTopic() {
        return topic;
    }

    public String getFile() {
        return file;
    }
}
