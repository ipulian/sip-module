package com.ipusoft.siplibrary.ifaceimpl;

import com.ipusoft.siplibrary.constant.AudioPlayType;
import com.ipusoft.siplibrary.iface.OnHeadsetPlugListener;

/**
 * author : GWFan
 * time   : 2/1/21 4:39 PM
 * desc   :
 */

public class OnHeadsetPlugListenerImpl implements OnHeadsetPlugListener {
    private static final String TAG = "OnHeadsetPlugListener";
    private static OnHeadsetPlugListener onHeadsetPlugListener;

    public static void setOnHeadsetPlugListener(OnHeadsetPlugListener listener) {
        onHeadsetPlugListener = listener;
    }

    @Override
    public void onHeadsetPlug(AudioPlayType audioPlayType) {
        if (onHeadsetPlugListener != null) {
            onHeadsetPlugListener.onHeadsetPlug(audioPlayType);
        }
    }
}
