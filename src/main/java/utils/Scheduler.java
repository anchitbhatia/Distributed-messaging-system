package utils;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private final int timeout;
    private Timer timer;

    public Scheduler(int timeout) {
        this.timeout = timeout;
        this.timer = null;
    }

    public void scheduleTask(TimerTask task) {
        this.cancelTask();
        this.timer = new Timer();
        this.timer.schedule(task, this.timeout);
    }

    public void cancelTask() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }
}
