package configs;

import api.Node;

public class ProducerConfig {
    private Node host;
    private Node broker;
    private String topic;
    private String file;

    public Node getHost() {
        return host;
    }

    public Node getBroker() {
        return broker;
    }

    public String getTopic() {
        return topic;
    }

    public String getFile() {
        return file;
    }
}
