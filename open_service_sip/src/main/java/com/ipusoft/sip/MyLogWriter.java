package com.ipusoft.sip;

import com.elvishew.xlog.XLog;
import com.ipusoft.utils.StringUtils;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * author : GWFan
 * time   : 1/14/21 3:22 PM
 * desc   :
 */


public class MyLogWriter extends LogWriter {
    private static final String TAG = "MyLogWriter";

    @Override
    public void write(LogEntry entry) {
        try {
            if (entry != null && StringUtils.isNotEmpty(entry.getMsg())) {
                XLog.d(entry.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
