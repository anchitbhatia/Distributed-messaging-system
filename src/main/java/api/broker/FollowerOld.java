package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.BrokerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Node;
import messages.Follower.FollowerRequest;

import java.io.IOException;
import java.net.Socket;

public class Follower extends BrokerOld implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(Follower.class);
    private final Node leaderNode;
    private Connection leaderConnection;
    private final Thread dataThread;

    public Follower(Node node, Node leader) throws IOException {
        super(node, Constants.TYPE_FOLLOWER);
        this.leaderNode = leader;
        this.dataThread = new Thread(new DataThread());
    }

    public void startServer(){
        super.startServer();
        this.dataThread.start();
    }

    private void connectLeader() throws IOException {
        this.leaderConnection = new Connection(leaderNode);
        FollowerRequest request = FollowerRequest.newBuilder().
                setHostName(this.node.getHostName()).
                setPort(this.node.getPort()).
                setId(this.node.getId()).
                build();
        Any packet = Any.pack(request);
        try {
            this.leaderConnection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            this.close();
        }
    }

    private BrokerRecord.BrokerMessage fetchLeader() throws IOException, ConnectionException {
        if (!leaderConnection.isClosed()) {
            byte[] record = leaderConnection.receive();
            try {
                Any packet = Any.parseFrom(record);
                return packet.unpack(BrokerRecord.BrokerMessage.class);
            } catch (NullPointerException e) {
                this.close();
                throw new ConnectionException("Connection closed!");
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void close() throws IOException {
        LOGGER.info("Closing connection to leader with id " + leaderNode.getId());
        this.leaderConnection.close();
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Server started");
            while (this.isBrokerRunning){
                Socket clientSocket = this.brokerSocket.accept();
                Connection connection = new Connection(clientSocket);
                Thread client = new Thread(new FollowerServer(this, connection));;
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DataThread implements Runnable{

        @Override
        public void run() {
            LOGGER.debug("Data thread started");
            try {
                connectLeader();
                newMember(leaderConnection);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!leaderConnection.isClosed()){
                BrokerRecord.BrokerMessage record = null;
                try {
                    record = fetchLeader();
                    if (record!=null){
                        ByteString data = record.getData();
                        if (data.size() != 0){
                            LOGGER.info("Received data: " + data.toStringUtf8());
                        }
                        else{
                            Thread.sleep(1000);
                        }
                    }
                }catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    try {
                        close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
//                LOGGER.debug("Received from leader" + leaderNode.getId() + " : " + Arrays.toString(record));
            }
        }
    }
}
