package api.broker;

import api.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.net.Socket;

public class Leader extends BrokerOld implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(Leader.class);

    public Leader(Node node) throws IOException {
        super(node, Constants.TYPE_LEADER);
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Server started");
            while (this.isBrokerRunning){
                Socket clientSocket = this.brokerSocket.accept();
                Connection connection = new Connection(clientSocket);
                Thread client = new Thread(new LeaderServer(this, connection));;
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
