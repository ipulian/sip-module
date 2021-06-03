package com.ipusoft.siplibrary.bean;

import com.ipusoft.siplibrary.constant.CallStatusCode;

/**
 * author : GWFan
 * time   : 6/2/21 3:57 PM
 * desc   :
 */

public enum SipCallStatus {

    STATUS_0(CallStatusCode.CODE_0, "无通话"),
    STATUS_1(CallStatusCode.CODE_1, "正在外呼"),
    STATUS_2(CallStatusCode.CODE_2, "正在呼入"),
    STATUS_3(CallStatusCode.CODE_3, "已振铃"),
    STATUS_4(CallStatusCode.CODE_4, "响应"),
    STATUS_5(CallStatusCode.CODE_5, "已接通"),
    STATUS_6(CallStatusCode.CODE_6, "已挂断");

    private final int status;
    private final String str;

    SipCallStatus(int status, String str) {
        this.status = status;
        this.str = str;
    }

    public int getStatus() {
        return status;
    }

    public String getStr() {
        return str;
    }
}
