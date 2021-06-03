package com.ipusoft.siplibrary;

import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.siplibrary.ifaceimpl.OnHeadsetPlugListenerImpl;
import com.ipusoft.siplibrary.receiver.HeadsetPlugReceiver;

import java.util.List;

/**
 * author : GWFan
 * time   : 5/31/21 2:41 PM
 * desc   :
 */

public class SipModuleApp extends SipCacheApp {

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


    /**
     * 通过反射调用
     *
     * @param listeners
     */
    public void initSipModule(List<BaseSipStatusChangedListener> listeners) {
        MySipPhoneEvent.registerSipStatusChangedListener(listeners);
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
