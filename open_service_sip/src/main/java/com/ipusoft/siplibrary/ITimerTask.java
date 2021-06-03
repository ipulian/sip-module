package com.ipusoft.siplibrary;

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

    public ITimerTask(long time, TimerTask task) {
        this.task = task;
        this.period = time;
        if (timer == null) {
            timer = new Timer();
        }
    }

    public void start() {
        timer.schedule(task, 0, period);//每隔time时间段就执行一次
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel();  //将原任务从队列中移除
            }
        }
    }
}

