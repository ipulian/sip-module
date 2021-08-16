package com.ipusoft.sip.iface;

/**
 * author : GWFan
 * time   : 6/1/21 9:22 AM
 * desc   :
 */

public interface OnSipCallOutWindowClickListener {

    void onDismiss();

    /**
     * 挂断
     */
    void hungUp();

    /**
     * 发送按键
     */
    void dialDTMF(String number);

    /**
     * 接听
     */
    void answerCall();

}
