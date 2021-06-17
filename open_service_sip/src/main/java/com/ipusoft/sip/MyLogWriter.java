package com.ipusoft.sip;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * author : GWFan
 * time   : 1/14/21 3:22 PM
 * desc   :
 */


public class MyLogWriter extends LogWriter {
    @Override
    public void write(LogEntry entry) {
        System.out.println(entry.getMsg());
    }
}
