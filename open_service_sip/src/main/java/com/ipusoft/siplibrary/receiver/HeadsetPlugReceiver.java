package com.ipusoft.siplibrary.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ipusoft.siplibrary.constant.AudioPlayType;
import com.ipusoft.siplibrary.iface.OnHeadsetPlugListener;

/**
 * author : GWFan
 * time   : 2/1/21 3:20 PM
 * desc   : 监听耳机连接状态
 */

public class HeadsetPlugReceiver extends BroadcastReceiver {
    private static final String TAG = "HeadsetPlugReceiver";

    private final OnHeadsetPlugListener mOnHeadsetPlugListener;


    public HeadsetPlugReceiver(OnHeadsetPlugListener onHeadsetPlugListener) {
        this.mOnHeadsetPlugListener = onHeadsetPlugListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            int state = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            if (BluetoothProfile.STATE_CONNECTED == state) {
                mOnHeadsetPlugListener.onHeadsetPlug(AudioPlayType.BLUETOOTH);
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                mOnHeadsetPlugListener.onHeadsetPlug(AudioPlayType.SPEAKER);
            }
        } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    //外放
                    mOnHeadsetPlugListener.onHeadsetPlug(AudioPlayType.SPEAKER);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    //耳机
                    mOnHeadsetPlugListener.onHeadsetPlug(AudioPlayType.HEADSET);
                }
            }
        }
    }
}
