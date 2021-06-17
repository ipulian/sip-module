package com.ipusoft.sip;

import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.sip.ifaceimpl.OnHeadsetPlugListenerImpl;
import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sip.receiver.HeadsetPlugReceiver;

import java.util.List;

/**
 * author : GWFan
 * time   : 5/31/21 2:41 PM
 * desc   :
 */

public class SipModuleApp extends SipCacheApp {

    private static final String TAG = "SipModuleApp";

    static {
        try {
            System.loadLibrary("pjsua2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initModule() {
        registerHeadsetPlugReceiver();
    }

    @Override
    public void unInitModule() {

    }

    /**
     * 通过反射调用
     *
     * @param listeners
     */
    public void registerSipListener(List<BaseSipStatusChangedListener> listeners) {
        MySipPhoneEvent.registerSipStatusChangedListener(listeners);
    }

    /**
     * 通过反射调用
     *
     * @param seatInfo
     */
    public void registerSipService(SeatInfo seatInfo) {
        SipPhoneManager.registerSip();
    }


    /**
     * 播放设备的listener
     */
    private void registerHeadsetPlugReceiver() {
        HeadsetPlugReceiver headsetPlugReceiver = new HeadsetPlugReceiver(new OnHeadsetPlugListenerImpl());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        AppContext.getAppContext().registerReceiver(headsetPlugReceiver, intentFilter);
    }
}
