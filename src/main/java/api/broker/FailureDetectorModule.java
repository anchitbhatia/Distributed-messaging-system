package api.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Scheduler;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FailureDetectorModule {
    private static final Logger LOGGER = LogManager.getLogger(FailureDetectorModule.class);
    private final Broker broker;
    private final Scheduler scheduler;

    public FailureDetectorModule(Broker broker) {
        this.broker = broker;
        this.scheduler = new Scheduler(Constants.FD_SCHEDULER_TIMER);
    }

    public void startModule() {
        this.scheduler.scheduleTask(new FailureDetectorTask());
    }

    public void stopModule() {
        this.scheduler.cancelTask();
    }

    private class FailureDetectorTask extends TimerTask {
        @Override
        public void run() {
            LOGGER.info("Failure detector task started");
            Map<Integer, Long> receiveTimes = broker.heartBeatModule.getHeartBeatReceiveTimes();
            LOGGER.info("Receive times " + receiveTimes);
            LOGGER.info("Current time " + System.nanoTime());
            if (receiveTimes!=null) {
                for (Map.Entry<Integer, Long> entry : receiveTimes.entrySet()) {
                    Integer id = entry.getKey();
                    Long time = entry.getValue();
                    long difference = TimeUnit.MILLISECONDS.convert(System.nanoTime() - time, TimeUnit.NANOSECONDS);
                    LOGGER.error("Time difference is " + difference);
                    if (difference > Constants.FAILURE_TIMEOUT) {
                        LOGGER.error("Broker " + id + " seems to be failed");
                        LOGGER.error("Time difference is " + difference);
                    }
                }
            }
            scheduler.scheduleTask(new FailureDetectorTask());
        }
    }
}
