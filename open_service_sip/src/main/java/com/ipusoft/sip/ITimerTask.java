package com.ipusoft.sip;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author : GWFan
 * time   : 1/13/21 3:36 PM
 * desc   : 计时器
 */

public class ITimerTask {
    private Timer timer;
    private final TimerTask task;
    private final long period;
    private final long delay;

    public ITimerTask(long period, TimerTask task) {
        this.task = task;
        this.period = period;
        this.delay = 0;
        if (timer == null) {
            timer = new Timer();
        }
    }

    public ITimerTask(long period, long delay, TimerTask task) {
        this.task = task;
        this.period = period;
        this.delay = delay;
        if (timer == null) {
            timer = new Timer();
        }
    }

    public void start() {
        timer.schedule(task, delay, period);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel();
            }
        }
    }
}

