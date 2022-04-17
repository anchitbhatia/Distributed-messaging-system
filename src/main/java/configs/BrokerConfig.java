package configs;

import utils.Node;

/***
 * Class to parse broker config
 * @author anchitbhatia
 */
public class BrokerConfig {
    private boolean isLeader;
    private Node host;
    private Node leader;

    public boolean isLeader() {
        return isLeader;
    }

    public Node getHost() {
        return host;
    }

    public Node getLeader() {
        return leader;
    }

}
