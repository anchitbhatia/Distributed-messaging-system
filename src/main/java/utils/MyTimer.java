package utils;

import api.ConsumerApi;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {

    public static Timer startTimer(ConsumerApi consumer){
        Timer timer = new Timer("Timer");
        timer.schedule(new Task(consumer), Constants.TIMEOUT);
        return timer;
    }

    private static class Task extends TimerTask {
        private final ConsumerApi consumer;

        public Task(ConsumerApi consumer) {
            this.consumer = consumer;
        }

        @Override
        public void run() {
            consumer.timedOut();
            System.out.println("\nTimer: Timed out");
        }
    }
}
