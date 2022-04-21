package api.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Scheduler;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FailureDetectorModule {
    private static final Logger LOGGER = LogManager.getLogger("FDmodule");
    private final Broker broker;
    private final Scheduler scheduler;

    public FailureDetectorModule(Broker broker) {
        LOGGER.debug("Initializing Failure detector module");
        this.broker = broker;
        this.scheduler = new Scheduler(Constants.FD_SCHEDULER_TIME);
    }

    public void startModule() {
        LOGGER.debug("Starting Failure detector module");
        this.scheduler.scheduleTask(new FailureDetectorTask());
    }

    public void stopModule() {
        this.scheduler.cancelTask();
    }

    private class FailureDetectorTask extends TimerTask {
        @Override
        public void run() {
            LOGGER.info("Detecting failures");
            Map<Integer, Long> receiveTimes = broker.heartBeatModule.getHeartBeatReceiveTimes();
            LOGGER.info("Receive times: " + receiveTimes);
            boolean failureDetected = false;
            if (receiveTimes!=null) {
                for (Map.Entry<Integer, Long> entry : receiveTimes.entrySet()) {
                    Integer id = entry.getKey();
                    Long time = entry.getValue();
                    long difference = TimeUnit.MILLISECONDS.convert(System.nanoTime() - time, TimeUnit.NANOSECONDS);
                    if (difference > Constants.FAILURE_TIMEOUT) {
                        failureDetected = true;
                        if (id == broker.leader.getId()) {
                            LOGGER.error("Leader " + id + " seems to be failed");
                        }
                        else {
                            LOGGER.error("Broker " + id + " seems to be failed");
                        }
//                        LOGGER.error("Time difference is " + difference);
                        broker.removeMember(id);
                    }
                }
            }
            if (!failureDetected) {
                LOGGER.info("No failure detected");
            }
            scheduler.scheduleTask(new FailureDetectorTask());
        }
    }
}
