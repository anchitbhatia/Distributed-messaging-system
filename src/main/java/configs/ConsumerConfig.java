package configs;

import utils.Node;

import java.util.List;

/***
 * Class to parse consumer config
 * @author anchitbhatia
 */
public class ConsumerConfig {
    private Node host;
    private List<Node> brokers;
    private String topic;
    private String type;
    private String file;
    private long startPosition;
    private long timeout;

    public Node getHost() {
        return host;
    }

    public List<Node> getBrokers() {
        return brokers;
    }

    public String getTopic() {
        return topic;
    }

    public String getType() {
        return type;
    }

    public Long getStartPosition() {
        return startPosition;
    }

    public String getFile() {
        return file;
    }

    public long getTimeout() {
        return timeout;
    }
}
