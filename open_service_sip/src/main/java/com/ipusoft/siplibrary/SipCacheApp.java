package com.ipusoft.siplibrary;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.siplibrary.bean.SipCallOutInfoBean;

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

}
