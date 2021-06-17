package com.ipusoft.sip.iface;


import com.ipusoft.sip.constant.AudioPlayType;

/**
 * author : GWFan
 * time   : 2/1/21 3:54 PM
 * desc   :
 */

public interface OnHeadsetPlugListener {
    /**
     * true说明没有耳机   false说明有耳机
     *
     * @param audioPlayType
     */
    void onHeadsetPlug(AudioPlayType audioPlayType);
}
