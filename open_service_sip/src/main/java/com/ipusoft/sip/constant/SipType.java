package com.ipusoft.sip.constant;

/**
 * author : GWFan
 * time   : 4/23/21 10:26 AM
 * desc   :
 */

public enum SipType {

    INIT("init"),
    LOGIN("login"),
    HTTP("http"),
    CALL_STATUS("call_status");

    private final String type;

    SipType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
