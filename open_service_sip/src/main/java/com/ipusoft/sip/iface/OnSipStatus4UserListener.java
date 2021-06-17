package com.ipusoft.sip.iface;

import com.ipusoft.context.bean.SipResponse;

/**
 * author : GWFan
 * time   : 6/3/21 10:05 AM
 * desc   :
 */

public interface OnSipStatus4UserListener {

    void onError(SipResponse sipResponse);

    void onStartCall();

    void onEndCall();
}
