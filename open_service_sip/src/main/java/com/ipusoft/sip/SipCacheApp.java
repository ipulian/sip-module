package com.ipusoft.sip;

import android.util.Log;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.bean.Customer;
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

    /**
     * sip呼入小号和渠道
     */
    private static String sipCallInVirtualNumber;
    private static String sipCallInChannel;
    private static Customer customer;

    public static void setSIPCallOutBean(SipCallOutInfoBean bean) {
        if (bean != null) {
            String phone = bean.getPhone();
            setSIPCallOutNumber(phone);
        } else {
            setSIPCallOutNumber("");
        }
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

    public static void setSipCallInNumberInfo(String virtualNumber, String channel, Customer customer1) {
        Log.d("TAG123", "setSipCallInNumberInfo: .----->" + virtualNumber + "---->" + channel);
        sipCallInVirtualNumber = virtualNumber;
        sipCallInChannel = channel;
        customer = customer1;
    }

    public static String getSipCallInVirtualNumber() {
        return sipCallInVirtualNumber;
    }

    public static String getSipCallInChannel() {
        return sipCallInChannel;
    }

    public static Customer getSipCallInCustomer() {
        return customer;
    }
}
