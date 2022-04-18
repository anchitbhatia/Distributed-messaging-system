//package api.broker;
//
//import api.Connection;
//import com.google.protobuf.Any;
//import messages.HeartBeat;
//import messages.ProducerRecord;
//import messages.Request;
//import messages.Subscribe;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import utils.Constants;
//import messages.Follower.FollowerRequest;
//
//import java.io.IOException;
//
//public class FollowerServer implements Runnable {
//    private static final Logger LOGGER = LogManager.getLogger(FollowerServer.class);
//    private final BrokerOld broker;
//    private final Connection connection;
//    private String connectionType;
//
//    public FollowerServer(BrokerOld broker, Connection connection) throws IOException {
//        this.broker = broker;
//        this.connection = connection;
//        this.connectionType = Constants.TYPE_NULL;
//    }
//
//    private void setConnectionType(Any packet) {
//        if (packet.is(ProducerRecord.ProducerMessage.class)) {
//            this.connectionType = Constants.TYPE_MESSAGE;
//        } else if (packet.is(Request.ConsumerRequest.class)) {
//            this.connectionType = Constants.TYPE_CONSUMER;
//        } else if (packet.is(Subscribe.SubscribeRequest.class)) {
//            this.connectionType = Constants.TYPE_SUBSCRIBER;
//        } else if (packet.is(FollowerRequest.class)) {
//            this.connectionType = Constants.TYPE_FOLLOWER;
//        } else if (packet.is(HeartBeat.HeartBeatMessage.class)) {
//            this.connectionType = Constants.TYPE_HEARTBEAT;
//        } else {
//            this.connectionType = Constants.TYPE_NULL;
//        }
//    }
//
//    private void newFollower(FollowerRequest packet) {
//
//    }
//
//    private void handleHeartBeat(HeartBeat.HeartBeatMessage message){
//        messages.Node.NodeDetails node = message.getNode();
//        LOGGER.info("Heartbeat received from " + node);
//        LOGGER.info("Members received: " + message.getMembersList());
//    }
//
//    @Override
//    public void run() {
//        LOGGER.info("Connection established " + connection);
//        while (!connection.isClosed()) {
//            byte[] message = connection.receive();
//            if (message != null) {
//                try {
//                    Any packet = Any.parseFrom(message);
//                    if (connectionType == null) {
//                        setConnectionType(packet);
//                    }
//                    LOGGER.debug("Received packet from " + connectionType);
//                    switch (this.connectionType) {
////                        case Constants.TYPE_PRODUCER -> Database.addQueue(packet.unpack(ProducerRecord.ProducerMessage.class));
////                        case Constants.TYPE_CONSUMER -> serveRequest(packet.unpack(Request.ConsumerRequest.class));
////                        case Constants.TYPE_SUBSCRIBER -> newSubscriber(packet.unpack(Subscribe.SubscribeRequest.class));
//                        case Constants.TYPE_FOLLOWER -> newFollower(packet.unpack(FollowerRequest.class));
//                        case Constants.TYPE_HEARTBEAT ->  handleHeartBeat(packet.unpack(HeartBeat.HeartBeatMessage.class));
//                        default -> LOGGER.info("Invalid client");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                LOGGER.info("Connection disconnected " + connection);
//                try {
//                    connection.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
