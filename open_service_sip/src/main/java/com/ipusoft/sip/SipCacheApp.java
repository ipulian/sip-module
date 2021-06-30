package com.ipusoft.sip;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.sip.bean.SipCallOutInfoBean;

/**
 * author : GWFan
 * time   : 5/31/21 2:41 PM
 * desc   :
 */

public abstract class SipCacheApp extends IpuSoftSDK {
    /**
     * SIP外呼的bean
     */
    private static SipCallOutInfoBean sipCallOutInfoBean;

    /**
     * SIP外呼的号码
     */
    private static String SIPCallOutNumber;

    /**
     * SIP外呼
     */
    private static String SIPCallId;

    public static void setSIPCallOutBean(SipCallOutInfoBean bean) {
        String phone = bean.getPhone();
        setSIPCallOutNumber(phone);
        SipCacheApp.sipCallOutInfoBean = bean;
    }

    public static SipCallOutInfoBean getSipCallOutInfoBean() {
        return SipCacheApp.sipCallOutInfoBean;
    }

    public static String getSIPCallOutNumber() {
        return SIPCallOutNumber;
    }

    public static void setSIPCallOutNumber(String callOutNumber) {
        SipCacheApp.SIPCallOutNumber = callOutNumber;
    }

    public static String getSIPCallId() {
        return SIPCallId;
    }

    public static void setSIPCallId(String SIPCallId) {
        SipCacheApp.SIPCallId = SIPCallId;
    }
}
