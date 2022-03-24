package configs;

import utils.Node;

public class ConsumerConfig {
    private Node host;
    private Node broker;
    private String topic;
    private String type;
    private String file;
    private long startPosition;
    private long timeout;

    public Node getHost() {
        return host;
    }

    public Node getBroker() {
        return broker;
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
